package com.xinhe.miaodai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sunfusheng.marqueeview.MarqueeView;
import com.xinhe.miaodai.R;
import com.xinhe.miaodai.activity.HtmlActivity;
import com.xinhe.miaodai.activity.LoginActivity;
import com.xinhe.miaodai.adapter.ItemAdapter;
import com.xinhe.miaodai.adapter.ProductAdapter;
import com.xinhe.miaodai.entity.ProductEntity;
import com.xinhe.miaodai.net.Api;
import com.xinhe.miaodai.net.ApiService;
import com.xinhe.miaodai.net.Contacts;
import com.xinhe.miaodai.net.HttpUrl;
import com.xinhe.miaodai.net.OnRequestDataListener;
import com.xinhe.miaodai.net.Params;
import com.xinhe.miaodai.net.RestClient;
import com.xinhe.miaodai.net.callback.IError;
import com.xinhe.miaodai.net.callback.IFailure;
import com.xinhe.miaodai.net.callback.ISuccess;
import com.xinhe.miaodai.utils.SPUtil;
import com.xinhe.miaodai.utils.ToastUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yanshihao
 */
public class HomeFragment extends Fragment {

    public final RequestOptions mRequestOptions =
            new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate();

    @BindView(R.id.fragment_home_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    Unbinder unbinder;

    private ProductAdapter mAdapter;

    private List<ProductEntity> mProductEntities;

    private List<ProductEntity> mRecommend;

    private MarqueeView mMarqueeView;
    private View mHeader2;
    private ItemAdapter mItemAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initText();
    }

    private void initText() {
        final List<String> text = new ArrayList<>();
        ApiService.GET_SERVICE(Api.NEWS, null, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject json) {
                JSONArray data = json.getJSONArray("data");
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    String body = jsonObject.getString("body");
                    text.add(body) ;
                }
                mMarqueeView.startWithList(text, R.anim.anim_bottom_in, R.anim.anim_top_out);
            }

            @Override
            public void requestFailure(int code, String msg) {

            }
        });
    }

    private void setImageView(ProductEntity productEntity, ImageView imageView) {
        Glide.with(getContext())
                .load(productEntity.getP_logo())
                .apply(mRequestOptions)
                .into(imageView);
    }

    private void start(ProductEntity product) {
        String token = SPUtil.getString(getActivity(), Contacts.TOKEN);
        if (TextUtils.isEmpty(token)) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.putExtra("html", product.getUrl());
            intent.putExtra("id", product.getId());
            intent.putExtra("title", product.getP_name());
            startActivity(intent);
        } else {
            ApiService.apply(product.getId(), token);
            Intent intent = new Intent(getContext(), HtmlActivity.class);
            intent.putExtra("html", product.getUrl());
            intent.putExtra("title", product.getP_name());
            startActivity(intent);
        }
    }


    private void initView() {
        mRecommend = new ArrayList<>();
        mProductEntities = new ArrayList<>();
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
                initText();
            }
        });

        mRefreshLayout.setEnableLoadMore(false);
        mAdapter = new ProductAdapter(mProductEntities);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration
                .Builder(getContext())
                .colorResId(R.color.divider_background)
                .size(20)
                .build()
        );

        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.e("itemclick", "onItemClick: ");
                ProductEntity product = mProductEntities.get(position);
                String token = SPUtil.getString(getActivity(), Contacts.TOKEN);

                if (TextUtils.isEmpty(token)) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.putExtra("html", product.getUrl());
                    intent.putExtra("id", product.getId());
                    intent.putExtra("title", product.getP_name());
                    startActivity(intent);
                } else {
                    ApiService.apply(product.getId(), token);
                    Intent intent = new Intent(getContext(), HtmlActivity.class);
                    intent.putExtra("html", product.getUrl());
                    intent.putExtra("title", product.getP_name());
                    startActivity(intent);
                }
            }
        });
        addHeader();

    }

    private void addHeader() {
        View header1 = View.inflate(getContext(), R.layout.item_header, null);
        mHeader2 = View.inflate(getContext(), R.layout.item_today, null);
        mMarqueeView = header1.findViewById(R.id.marqueeView);
        mAdapter.addHeaderView(header1, 0);
        mAdapter.addHeaderView(mHeader2, 1);
        RecyclerView recyclerView = mHeader2.findViewById(R.id.item_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration
                .Builder(getContext())
                .colorResId(R.color.white)
                .size(30)
                .build());
        mItemAdapter = new ItemAdapter(null);
        recyclerView.setAdapter(mItemAdapter);
        mItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.e("itemclick", "onItemClick: ");
                ProductEntity product = mItemAdapter.getData().get(position);
                String token = SPUtil.getString(getActivity(), Contacts.TOKEN);

                if (TextUtils.isEmpty(token)) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.putExtra("html", product.getUrl());
                    intent.putExtra("id", product.getId());
                    intent.putExtra("title", product.getP_name());
                    startActivity(intent);
                } else {
                    ApiService.apply(product.getId(), token);
                    Intent intent = new Intent(getContext(), HtmlActivity.class);
                    intent.putExtra("html", product.getUrl());
                    intent.putExtra("title", product.getP_name());
                    startActivity(intent);
                }
            }
        });
    }

    private void initData() {
        mProductEntities.clear();
        RestClient.builder().url(HttpUrl.PRODUCT_HOT)
                .params("name", Params.getAppName())
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        mRefreshLayout.finishRefresh();
                        mRefreshLayout.finishLoadMore();
                        ToastUtils.showToast("网络异常，刷新试试");
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        mRefreshLayout.finishRefresh();
                        mRefreshLayout.finishLoadMore();
                        ToastUtils.showToast(msg);
                    }
                }).success(new ISuccess() {
            @Override
            public void onSuccess(String response) {
                JSONArray jsonArray = JSONObject.parseObject(response).getJSONArray("data");
                List<ProductEntity> entities = JSON.parseArray(JSON.toJSONString(jsonArray), ProductEntity.class);
                ProductEntity productEntity = entities.get(0);
                productEntity.setItemType(1);
                mProductEntities.addAll(entities);
                mAdapter.notifyDataSetChanged();
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
            }
        }).build().post();

        ApiService.GET_SERVICE(Api.RECOMMEND, null, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject json) {
                JSONArray data = json.getJSONArray("data");
                final List<ProductEntity> entities = JSON.parseArray(data.toJSONString(), ProductEntity.class);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mItemAdapter.setNewData(entities);
                    }
                });
            }

            @Override
            public void requestFailure(int code, String msg) {
                ToastUtils.showToast(msg);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}