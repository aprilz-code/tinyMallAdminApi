package com.aprilz.tiny.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.consts.Const;
import com.aprilz.tiny.common.exception.ServiceException;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.common.utils.UserUtil;
import com.aprilz.tiny.mall.CouponConstant;
import com.aprilz.tiny.mapper.ApCouponMapper;
import com.aprilz.tiny.mbg.entity.*;
import com.aprilz.tiny.service.IApCouponService;
import com.aprilz.tiny.service.IApCouponUserService;
import com.aprilz.tiny.service.IApGoodsService;
import com.aprilz.tiny.vo.CouponVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券信息及规则表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-14
 */
@Service
public class ApCouponServiceImpl extends ServiceImpl<ApCouponMapper, ApCoupon> implements IApCouponService {


    @Resource
    private IApCouponUserService couponUserService;

    @Autowired
    private IApGoodsService goodsService;


    @Override
    public void assignForRegister(Long userId) {
        //查询注册可领用的优惠券
        LambdaQueryWrapper<ApCoupon> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApCoupon::getType, Const.TYPE_USE).eq(ApCoupon::getDeleteFlag, Const.TYPE_REGISTER);
        List<ApCoupon> list = this.list(queryWrapper);

        list.forEach(coupon -> {
            Long couponId = coupon.getId();
            LambdaQueryWrapper<ApCouponUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ApCouponUser::getCouponId, couponId);
            wrapper.eq(ApCouponUser::getUserId, userId);
            wrapper.eq(ApCouponUser::getDeleteFlag, 1);
            long count = couponUserService.count(wrapper);
            if (count > 0) {
                return;
            }
            Integer limit = coupon.getLimit();
            while (limit > 0) {
                ApCouponUser couponUser = new ApCouponUser();
                couponUser.setCouponId(couponId);
                couponUser.setUserId(userId);
                Integer timeType = coupon.getTimeType();
                if (Objects.equals(Const.TIME_TYPE_TIME, timeType)) {
                    couponUser.setStartTime(coupon.getStartTime());
                    couponUser.setEndTime(coupon.getEndTime());
                } else {
                    couponUser.setStartTime(new Date());
                    couponUser.setEndTime(DateUtil.offsetDay(new Date(), coupon.getDays()).toJdkDate());
                }
                couponUserService.save(couponUser);
                limit--;
            }

        });

    }

    @Override
    public IPage<CouponVo> queryMyList(Integer couponId, Short status, Integer page, Integer size, String sort, String order) {
        ApAdmin user = UserUtil.getUser();
        if (Objects.isNull(user)) {
            throw new ServiceException();
        }
        // 构造分页对象

        Page<CouponVo> pages = new Page(page, size);
        QueryWrapper<CouponVo> queryWrapper = new QueryWrapper<>();
        if (Objects.nonNull(couponId)) {
            queryWrapper.eq("t.id", couponId);
        }
        if (Objects.nonNull(status)) {
            queryWrapper.eq("u.status", status);
        }
        queryWrapper.eq("u.user_id", user.getId());

        queryWrapper.eq("u.delete_flag", false);
        queryWrapper.eq("t.delete_flag", false);

        if ("desc".equals(order)) {
            queryWrapper.orderByDesc("u." + sort);
        } else {
            queryWrapper.orderByAsc("u." + sort);
        }

        IPage<CouponVo> productPage = this.baseMapper.getPageVo(pages, queryWrapper);
        return productPage;
    }

    /**
     * @param
     * @return java.util.List
     * @author aprilz
     * @description 过滤掉用户已领取过的优惠券，并最多三条
     * @since 2022/7/19
     **/
    @Override
    public List<ApCoupon> queryAvailableList(Long userId) {
        return this.baseMapper.queryAvailableList(userId);
    }

    @Override
    public ApCoupon checkCoupon(Long userId, Long couponId, Long userCouponId, BigDecimal checkedGoodsPrice, List<ApCart> cartList) {
        ApCoupon coupon = this.lambdaQuery().eq(ApCoupon::getDeleteFlag, false).eq(ApCoupon::getId, couponId).one();
        if (coupon == null || !coupon.getDeleteFlag()) {
            return null;
        }

        ApCouponUser couponUser = couponUserService.lambdaQuery().eq(ApCouponUser::getDeleteFlag, false).eq(ApCouponUser::getId, userCouponId).one();
        if (couponUser == null || !couponUser.getDeleteFlag()) {
            couponUser = couponUserService.queryOne(userId, couponId);
        } else if (!couponId.equals(couponUser.getCouponId())) {
            return null;
        }

        if (couponUser == null) {
            return null;
        }

        // 检查是否超期
        Integer timeType = coupon.getTimeType();
        Integer days = coupon.getDays();
        Date now = new Date();
        if (timeType.equals(CouponConstant.TIME_TYPE_TIME)) {
            if (now.getTime() < coupon.getStartTime().getTime() || now.getTime() > coupon.getEndTime().getTime()) {
                return null;
            }
        } else if (timeType.equals(CouponConstant.TIME_TYPE_DAYS)) {
            DateTime expired = DateUtil.offsetDay(couponUser.getCreateTime(), days);
            if (now.getTime() > expired.getTime()) {
                return null;
            }
        } else {
            return null;
        }

        // 检测商品是否符合
        Map<Long, List<ApCart>> cartMap = new HashMap<>();
        //可使用优惠券的商品或分类
        List<Integer> goodsValueList = new ArrayList(Arrays.asList(coupon.getGoodsValue()));
        Integer goodType = coupon.getGoodsType();

        if (goodType.equals(CouponConstant.GOODS_TYPE_CATEGORY) ||
                goodType.equals((CouponConstant.GOODS_TYPE_ARRAY))) {
            for (ApCart cart : cartList) {
                Long key = goodType.equals(CouponConstant.GOODS_TYPE_ARRAY) ? cart.getGoodsId() :
                        goodsService.lambdaQuery().eq(ApGoods::getDeleteFlag, false).eq(ApGoods::getId, cart.getGoodsId()).one().getCategoryId();
                List<ApCart> carts = cartMap.get(key);
                if (carts == null) {
                    carts = new LinkedList<>();
                }
                carts.add(cart);
                cartMap.put(key, carts);
            }
            //购物车中可以使用优惠券的商品或分类
            goodsValueList.retainAll(cartMap.keySet());
            //可使用优惠券的商品的总价格
            BigDecimal total = new BigDecimal(0);

            for (Integer goodsId : goodsValueList) {
                List<ApCart> carts = cartMap.get(goodsId);
                for (ApCart cart : carts) {
                    total = total.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
                }
            }
            //是否达到优惠券满减金额
            if (total.compareTo(coupon.getMin()) == -1) {
                return null;
            }
        }

        // 检测订单状态
        Integer status = coupon.getStatus();
        if (!status.equals(CouponConstant.STATUS_NORMAL)) {
            return null;
        }
        // 检测是否满足最低消费
        if (checkedGoodsPrice.compareTo(coupon.getMin()) == -1) {
            return null;
        }

        return coupon;

    }

    @Override
    public Page<ApCoupon> queryList(Integer page, Integer size, String sort, String order) {
        LambdaQueryChainWrapper<ApCoupon> lambdaQuery = this.lambdaQuery();
        ApAdmin user = UserUtil.getUser();
        if (Objects.nonNull(user)) {
            List<ApCouponUser> list = couponUserService.lambdaQuery().eq(ApCouponUser::getUserId, user.getId()).eq(ApCouponUser::getDeleteFlag, false)
                    .select(ApCouponUser::getCouponId).list();
            List<Long> collect = list.stream().map(ApCouponUser::getCouponId).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(collect)) {
                lambdaQuery.notIn(ApCoupon::getId, collect);
            }
        }

        return lambdaQuery.eq(ApCoupon::getDeleteFlag, false).eq(ApCoupon::getType, CouponConstant.TYPE_COMMON).eq(ApCoupon::getStatus, CouponConstant.STATUS_NORMAL)
                .page(PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(size).setSort(sort).setOrder(order)));
    }

    @Override
    public ApCoupon findByCode(String code) {
        return this.lambdaQuery().eq(ApCoupon::getCode, code).eq(ApCoupon::getType, CouponConstant.TYPE_CODE).eq(ApCoupon::getStatus, CouponConstant.STATUS_NORMAL)
                .eq(ApCoupon::getDeleteFlag, false).one();
    }

    @Override
    public String generateCode() {
        String code = getRandomNum(8);
        while (findByCode(code) != null) {
            code = getRandomNum(8);
        }
        return code;
    }

    @Override
    public Page<ApCoupon> querySelective(String name, Short type, Short status, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApCoupon> query = this.lambdaQuery();

        if (StrUtil.isNotBlank(name)) {
            query.like(ApCoupon::getName, name);
        }
        if (Objects.nonNull(type)) {
            query.eq(ApCoupon::getType, type);
        }
        if (Objects.nonNull(status)) {
            query.eq(ApCoupon::getStatus, status);
        }
        query.eq(ApCoupon::getDeleteFlag, false);
        Page<ApCoupon> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);

    }

    private String getRandomNum(Integer num) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        base += "0123456789";

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
