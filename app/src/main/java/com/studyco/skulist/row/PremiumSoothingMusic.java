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
package com.studyco.skulist.row;

import com.android.billingclient.api.BillingClient.SkuType;
import com.studyco.R;
import com.studyco.billing.BillingProvider;

/**
 * Handles Ui specific to "premium" - non-consumable in-app item row
 */
public class PremiumSoothingMusic extends UiManagingDelegate {
    public static final String SKU_ID = "premium_soothing_music";

    public PremiumSoothingMusic(BillingProvider billingProvider) {
        super(billingProvider);
    }

    @Override
    public @SkuType
    String getType() {
        return SkuType.SUBS;
    }

    @Override
    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        super.onBindViewHolder(data, holder);
        int textId = mBillingProvider.isSoothingMusicSubscribed() ? R.string.button_own
                : R.string.button_buy;
        holder.button.setText(textId);
        holder.skuIcon.setImageResource(R.drawable.music);
    }

    @Override
    public void onButtonClicked(SkuRowData data) {
        if (data != null && mBillingProvider.isSoothingMusicSubscribed()) {
            showAlreadyPurchasedToast();
        }  else {
            super.onButtonClicked(data);
        }
    }
}

