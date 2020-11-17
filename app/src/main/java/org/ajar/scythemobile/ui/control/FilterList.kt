package org.ajar.scythemobile.ui.control

import androidx.lifecycle.MutableLiveData

interface FilterList {

    val selected: MutableLiveData<MutableList<Int>>
}