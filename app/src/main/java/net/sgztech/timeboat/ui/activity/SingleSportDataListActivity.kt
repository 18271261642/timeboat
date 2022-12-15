package net.sgztech.timeboat.ui.activity

import android.view.View
import androidx.activity.viewModels
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.imlaidian.utilslibrary.utils.DateUtil
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.StringsUtils
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.databinding.ActivitySingleSportListBinding
import net.sgztech.timeboat.provide.viewModel.SingleSportListViewModel
import net.sgztech.timeboat.ui.adapter.SportInfoListAdapter

class SingleSportDataListActivity :BaseActivity() {
    private val TAG = SingleSportDataListActivity::class.java.simpleName
    private val viewModel :SingleSportListViewModel by viewModels()
    private val binding :ActivitySingleSportListBinding by viewBinding()
    private var startDate:String? =""
    private var endDate :String? =""
    private var sportName :String? =""
    private var sportType =0
    private var sportIconUrl :String? =""

    private var resultsAdapter : SportInfoListAdapter?=null

    override fun getLayoutId(): Int {
       return  R.layout.activity_single_sport_list
    }

    override fun initBindView() {

        startDate = intent.getStringExtra(Constants.SPORT_TYPE_START_DATE)
        endDate = intent.getStringExtra(Constants.SPORT_TYPE_END_DATE)
        sportName =intent.getStringExtra(Constants.SPORT_TYPE_NAME)
        sportType =intent.getIntExtra(Constants.SPORT_TYPE_VALUE,0)
        sportIconUrl =intent.getStringExtra(Constants.SPORT_TYPE_ICON_URL)

        binding.sportInfoTitleBar.backArrow.setOnClickListener(this)
        binding.sportInfoTitleBar.titleName.text=sportName
        try {
            binding.startDate.text = DateUtil.getTimeStampYMD(StringsUtils.toDate(startDate).time)
            binding.endDate.text= DateUtil.getTimeStampYMD(StringsUtils.toDate(endDate).time)
        }catch (e:Exception){
            e.printStackTrace()
        }
        resultsAdapter = SportInfoListAdapter(sportType, sportName ,sportIconUrl) { result, position ->


        }
        configureResultList()
    }

    private fun configureResultList() {
        with(binding.sportList) {
            setHasFixedSize(true)
            itemAnimator = null
            adapter = resultsAdapter
        }

    }

    private fun observer(){

        viewModel.sportListData.vmObserver(this){
            onAppLoading = {

            }
            onAppSuccess = {
                if(it!=null&& it.isNotEmpty()){
                    resultsAdapter!!.refreshData(it)
                }else{
                    UToast.showShortToast("无数据")
                }
            }
            onAppError = { msg, errorCode ->

            }

            onAppComplete ={

            }

        }

    }

    override fun initData() {
        super.initData()
        observer()
        if(startDate!=null && endDate!=null&&sportType!=0){
            viewModel.getSportData(startDate!!, endDate!! ,sportType  )
        }else{
            LogUtil.d(TAG, "startDate=" +startDate + ",endDate=" +endDate + ",sportType =" +sportType)
        }

    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        when(v.id){
            R.id.back_arrow -> finish()
        }
    }
}