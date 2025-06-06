package com.jerry.ronaldo.siufascore.base

import android.os.Bundle

interface ViewState {
}
interface ViewStateSaver<S:ViewState>{
    fun S.toBundle():Bundle
    fun restore(bundle:Bundle?):S
}