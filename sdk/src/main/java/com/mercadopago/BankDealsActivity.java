package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mercadopago.adapters.BankDealsAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.BankDeal;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankDealsActivity extends AppCompatActivity {

    // Activity parameters
    protected String mMerchantPublicKey;

    // Local vars
    protected Activity mActivity;
    protected MercadoPago mMercadoPago;
    protected RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView();

        // Set activity
        mActivity = this;

        // Get activity parameters
        mMerchantPublicKey = mActivity.getIntent().getStringExtra("merchantPublicKey");

        // Init MercadoPago object with public key
        mMercadoPago = new MercadoPago.Builder()
                .setContext(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .build();

        // Set recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.bank_deals_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL_LIST));

        // Set a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        // Get bank deals
        getBankDeals();
    }

    protected void setContentView() {

        setContentView(R.layout.activity_bank_deals);
    }

    public void refreshLayout(View view) {
        getBankDeals();
    }

    private void getBankDeals() {

        LayoutUtil.showProgressLayout(mActivity);
        Call<List<BankDeal>> call = mMercadoPago.getBankDeals();
        call.enqueue(new Callback<List<BankDeal>>() {
            @Override
            public void onResponse(Call<List<BankDeal>> call, Response<List<BankDeal>> response) {

                if (response.isSuccessful()) {

                    mRecyclerView.setAdapter(new BankDealsAdapter(mActivity, response.body(), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            BankDeal selectedBankDeal = (BankDeal) view.getTag();
                            Intent intent = new Intent(mActivity, TermsAndConditionsActivity.class);
                            intent.putExtra("termsAndConditions", selectedBankDeal.getLegals());
                            startActivity(intent);
                        }
                    }));

                    LayoutUtil.showRegularLayout(mActivity);

                } else {

                    ApiUtil.finishWithApiException(mActivity, response);
                }
            }

            @Override
            public void onFailure(Call<List<BankDeal>> call, Throwable t) {

                ApiUtil.finishWithApiException(mActivity, t);
            }
        });
    }
}
