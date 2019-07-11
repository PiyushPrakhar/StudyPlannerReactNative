/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.studyco.Dashboard;

import android.content.SharedPreferences;
import android.util.Log;

import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.Purchase;
import com.studyco.billing.BillingConstants;
import com.studyco.billing.BillingManager.BillingUpdatesListener;
import com.studyco.skulist.row.PremiumLinearTracking;
import com.studyco.skulist.row.PremiumPlannerAI;
import com.studyco.skulist.row.PremiumSoothingMusic;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MainViewControllerDashboard {
    private static final String TAG = "MainViewControllerDash";


    private final UpdateListener mUpdateListener;
    private DashboardActivity mActivity;

    // Tracks if we currently own subscriptions SKUs

    private boolean premium_planner_ai;
    private boolean premium_soothing_music;
    private boolean premium_linear_tracking;

    public MainViewControllerDashboard(DashboardActivity activity) {
        mUpdateListener = new UpdateListener();
        mActivity = activity;
        loadData();
    }

    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }


    public boolean isLinearTrackSubscribed() {
        return premium_linear_tracking;
    }

    public boolean isPlannerAiSubscribed() {
        return premium_planner_ai;
    }

    public boolean isSoothingMusicSubscribed() {
        return premium_soothing_music;
    }

    /**
     * Handler to billing updates
     */
    private class UpdateListener implements BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {

        }

        @Override
        public void onConsumeFinished(String token, @BillingResponse int result) {
            Log.d(TAG, "Consumption finished. Purchase token: " + token + ", result: " + result);

            if (result == BillingResponse.OK) {

                // Successfully consumed

                Log.d(TAG, "Consumption successful. Provisioning.");
                saveData();
            }else {

                Log.d(TAG, "Consumption failed. Provisioning.");
            }

            Log.d(TAG, "End consumption flow.");
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchaseList) {
            premium_soothing_music= false;
            premium_planner_ai= false;
            premium_linear_tracking =false;
            SharedPreferences.Editor spe = mActivity.getPreferences(MODE_PRIVATE).edit();
            for (Purchase purchase : purchaseList) {
                switch (purchase.getSku()) {
                    case PremiumLinearTracking.SKU_ID:
                        Log.d(TAG, "Premium linear tracking subscribed");
                        spe.putBoolean(BillingConstants.SKU_LINEAR_TRACKING, true);
                        spe.apply();
                        premium_linear_tracking = true;
                        break;
                    case PremiumPlannerAI.SKU_ID:
                        Log.d(TAG, "Premium planner AI subscribed");
                        spe.putBoolean(BillingConstants.SKU_PLANNER_AI, true);
                        spe.apply();
                        premium_planner_ai =true;
                        break;
                    case PremiumSoothingMusic.SKU_ID:
                        Log.d(TAG, "Premium soothing music subscribed");
                        premium_soothing_music =true;
                        spe.putBoolean(BillingConstants.SKU_SOOTHING_MUSIC, true);
                        spe.apply();
                        break;
                }
            }
        }
    }

    /**
     * Save current tank level to disc
     *
     * Note: In a real application, we recommend you save data in a secure way to
     * prevent tampering.
     * For simplicity in this sample, we simply store the data using a
     * SharedPreferences.
     */
    private void saveData() {
       /* SharedPreferences.Editor spe = mActivity.getPreferences(MODE_PRIVATE).edit();
        spe.putInt("tank", mTank);
        spe.apply();
        Log.d(TAG, "Saved data: tank = " + String.valueOf(mTank));*/
    }

    private void loadData() {
       /* SharedPreferences sp = mActivity.getPreferences(MODE_PRIVATE);
        mTank = sp.getInt("tank", 2);
        Log.d(TAG, "Loaded data: tank = " + String.valueOf(mTank));*/
    }
}