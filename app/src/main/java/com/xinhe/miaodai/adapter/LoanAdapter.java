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
import com.xinhe.miaodai.view.YStarView;

import java.util.List;

/**
 * - @Author:  闫世豪
 * - @Time:  2018/5/23 下午6:10
 * - @Email whynightcode@gmail.com
 */
public class LoanAdapter extends BaseQuickAdapter<ProductEntity, BaseViewHolder> {
    private final RequestOptions mRequestOptions =
            new RequestOptions()
                    .centerCrop()
                    .transform(new GlideCircleTransform())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate();

    public LoanAdapter(@Nullable List<ProductEntity> data) {
        super(R.layout.item_list, data);
      //  openLoadAnimation();
        //多次执行动画
        isFirstOnly(false);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProductEntity item) {

        helper.setText(R.id.item_rate_number,  item.getMin_algorithm() + "%");
        helper.setText(R.id.item_name, item.getP_name())
                .setText(R.id.item_desc, item.getP_desc())
                .setText(R.id.item_person_number, item.getApply() + "人")
                .setText(R.id.item_speed_number,  item.getFastest_time())
        ;
        YStarView yStarView = helper.getView(R.id.ystarview);
        yStarView.setStarCount(5);
        yStarView.setRating(4);
        yStarView.setHalf(true);
        yStarView.setStar(R.drawable.ic_full,R.drawable.ic_half);

        Glide.with(mContext)
                .load(item.getP_logo())
                .apply(mRequestOptions)
                .into((ImageView) helper.getView(R.id.item_logo));

        helper.addOnClickListener(R.id.item_btn_apply);
    }
}
