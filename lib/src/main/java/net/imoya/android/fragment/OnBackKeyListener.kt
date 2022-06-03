/*
 * Copyright (C) 2022 IceImo-P
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.imoya.android.fragment

import androidx.fragment.app.Fragment

/**
 * [Fragment] 用、戻るキーリスナ
 */
interface OnBackKeyListener {
    /**
     * 戻るキーが押された時の処理
     *
     * @return 処理を実行した(呼び出し元の処理が不要の)場合はtrue, 呼び出し元の処理に任せる場合はfalse
     */
    fun onBackKeyPressed(): Boolean
}