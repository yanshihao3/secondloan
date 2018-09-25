package com.xinhe.miaodai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xinhe.miaodai.R;
import com.xinhe.miaodai.activity.HtmlActivity;
import com.xinhe.miaodai.activity.LoginActivity;
import com.xinhe.miaodai.adapter.LoanAdapter;
import com.xinhe.miaodai.entity.ProductEntity;
import com.xinhe.miaodai.net.Api;
import com.xinhe.miaodai.net.ApiService;
import com.xinhe.miaodai.net.Contacts;
import com.xinhe.miaodai.net.OnRequestDataListener;
import com.xinhe.miaodai.net.Params;
import com.xinhe.miaodai.utils.SPUtil;
import com.xinhe.miaodai.utils.ToastUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yanshihao
 */
public class LoanFragment extends Fragment {

    @BindView(R.id.fragment_loan_rv)
    RecyclerView mFragmentLoanRv;
    @BindView(R.id.loan_refresh)
    SmartRefreshLayout mLoanRefresh;
    Unbinder unbinder;

    private List<ProductEntity> mList;

    private LoanAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loan, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData(0, 5, 0);
    }

    private void initData(int offset, int limit, final int type) {
        HashMap<String, String> params = new HashMap<>();
        params.put("name", Params.getAppName());
        params.put("offset", offset + "");
        params.put("number", limit + "");
        ApiService.GET_SERVICE(Api.PRODUCT_LSIT, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject json) {
                JSONArray jsonArray = json.getJSONArray("data");
                List<ProductEntity> entities = JSON.parseArray(JSON.toJSONString(jsonArray), ProductEntity.class);
                if (type == 0) {
                    mAdapter.setNewData(entities);
                } else {
                    if (entities.size() > 0) {
                        mAdapter.addData(entities);
                    }
                }
                if (mLoanRefresh.isEnableRefresh()) {
                    mLoanRefresh.finishRefresh();
                }
                if (mLoanRefresh.isEnableLoadMore()) {
                    mLoanRefresh.finishLoadMore();


                }
            }

            @Override
            public void requestFailure(int code, String msg) {
                mLoanRefresh.finishLoadMore();
                mLoanRefresh.finishRefresh();
                ToastUtils.showToast(msg);
            }
        });
    }

    private void initView() {
        mList = new ArrayList<>();
        mAdapter = new LoanAdapter(null);
        mLoanRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData(0, 8, 0);
            }
        });
        mLoanRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                initData(mAdapter.getData().size(), 10, 1);
            }
        });
        mFragmentLoanRv.setLayoutManager(new LinearLayoutManager(getContext()));

        mFragmentLoanRv.addItemDecoration(new HorizontalDividerItemDecoration
                .Builder(getContext())
                .colorResId(R.color.divider_background)
                .size(20)
                .build()
        );
        mFragmentLoanRv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.e("itemclick", "onItemClick: ");
                ProductEntity product = mAdapter.getData().get(position);
                start(product);
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //跳转登录或者html界面
                ProductEntity product = mAdapter.getData().get(position);
                start(product);
            }
        });

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void apply(String productId, String token) {
        OkGo.<String>post(Api.APPLY)
                .params("id", productId)
                .params("token", token)
                .params("name", Params.getAppName())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("apply", "onSuccess: " + response.body());
                    }
                });
    }
}