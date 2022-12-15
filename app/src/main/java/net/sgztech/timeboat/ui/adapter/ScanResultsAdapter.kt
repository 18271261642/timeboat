package net.sgztech.timeboat.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.device.rxble.scan.ScanResult
import net.sgztech.timeboat.R


internal class ScanResultsAdapter(
    private val onClickListener: (ScanResult,Int) -> Unit
) : RecyclerView.Adapter<ScanResultsAdapter.ViewHolder>() {

    private val TAG =ScanResultsAdapter::class.java.simpleName

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val device: TextView = itemView.findViewById(R.id.device)
        val rssi: TextView = itemView.findViewById(R.id.rssi)
        val listItem :LinearLayout  = itemView.findViewById(R.id.ble_item_view)
    }

    private val data = mutableListOf<ScanResult>()

    fun addScanResult(bleScanResult: ScanResult) {
        // Not the best way to ensure distinct devices, just for the sake of the demo.
        data.withIndex()
            .firstOrNull { it.value.bleDevice == bleScanResult.bleDevice  }
            ?.let {
                 //device already in data list => update
                data[it.index] = bleScanResult
                Log.d(TAG, "index=" +it.index + ",device mac" + it.value.bleDevice.macAddress)
                notifyItemChanged(it.index)
            }?: run {
                // new device => add to data list
                with(data) {
//                    if(bleScanResult.bleDevice.name!=null&&bleScanResult.bleDevice.name!!.contains("QT")){
//                        add(bleScanResult)
//                    }
                    add(bleScanResult)
                    sortBy { it.bleDevice.macAddress }
                }
                notifyDataSetChanged()
            }
    }

    fun clearScanResults() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(data[position]) {
            holder.device.text = String.format("%s (%s)", bleDevice.macAddress, scanRecord.deviceName)
            holder.rssi.text = String.format("RSSI: %d", rssi)
            holder.itemView.setOnClickListener { onClickListener(this,position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.scan_ble_list_item, parent, false)
            .let { ViewHolder(it) }

}
