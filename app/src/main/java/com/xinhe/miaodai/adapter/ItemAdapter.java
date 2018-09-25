package com.xinhe.miaodai.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinhe.miaodai.R;
import com.xinhe.miaodai.entity.ProductEntity;

import java.util.List;

/**
 * - @Author:  闫世豪
 * - @Time:  2018/5/23 下午6:10
 * - @Email whynightcode@gmail.com
 */
public class ItemAdapter extends BaseQuickAdapter<ProductEntity, BaseViewHolder> {
    private final RequestOptions mRequestOptions =
            new RequestOptions()
                    .centerCrop()
                    .transform(new GlideCircleTransform())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate();

    public ItemAdapter(@Nullable List<ProductEntity> data) {
        super(R.layout.item_like, data);
        openLoadAnimation();
        //多次执行动画
        isFirstOnly(false);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProductEntity item) {

        helper.setText(R.id.item_like_name, item.getP_name())
                .setText(R.id.item_like_number, getMoney(item))
        ;

        Glide.with(mContext)
                .load(item.getP_logo())
                .apply(mRequestOptions)
                .into((ImageView) helper.getView(R.id.item_like_logo));

    }

    private String getMoney(ProductEntity item) {
        String maximumAmount = item.getMaximum_amount();
        String minimum_amount = item.getMinimum_amount();
        if (minimum_amount.length() > 4) {
            String substring = minimum_amount.substring(0, minimum_amount.length() - 4);
            minimum_amount = substring + "万";
        }
        if (maximumAmount.length() > 4) {
            String substring = maximumAmount.substring(0, maximumAmount.length() - 4);
            maximumAmount = substring + "万";
        }
        return "¥" + minimum_amount + "-" + "¥" + maximumAmount;
    }
}
