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
import com.studyco.billing.BillingProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This factory is responsible to finding the appropriate delegate for Ui rendering and calling
 * corresponding method on it.
 */
public class UiDelegatesFactory {
    private final Map<String, UiManagingDelegate> uiDelegates;

    public UiDelegatesFactory(BillingProvider provider) {
        uiDelegates = new LinkedHashMap<>();
        uiDelegates.put(PremiumSoothingMusic.SKU_ID, new PremiumSoothingMusic(provider));
        uiDelegates.put(PremiumPlannerAI.SKU_ID, new PremiumPlannerAI(provider));
        uiDelegates.put(PremiumLinearTracking.SKU_ID, new PremiumLinearTracking(provider));
    }

    /**
     * Returns the list of all SKUs for the billing type specified
     */
    public final ArrayList<String> getSkuList(@SkuType String billingType) {
        ArrayList<String> result = new ArrayList<>();
        for (String skuId : uiDelegates.keySet()) {
            UiManagingDelegate delegate = uiDelegates.get(skuId);
            if (delegate.getType().equals(billingType)) {
                result.add(skuId);
            }
        }
        return result;
    }

    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        uiDelegates.get(data.getSku()).onBindViewHolder(data, holder);
    }

    public void onButtonClicked(SkuRowData data) {
        uiDelegates.get(data.getSku()).onButtonClicked(data);
    }
}