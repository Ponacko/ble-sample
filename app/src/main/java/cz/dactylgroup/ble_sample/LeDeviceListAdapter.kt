package cz.dactylgroup.ble_sample

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.BaseAdapter
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.View
import android.widget.TextView




/**
 * @author Tomáš Stolárik <tomas.stolarik@dactylgroup.com>
 */
// Adapter for holding devices found through scanning.
class LeDeviceListAdapter(private val context: Context) : BaseAdapter() {
    private val mLeDevices: ArrayList<BluetoothDevice> = ArrayList()
    private lateinit var inflater: LayoutInflater

    fun addDevice(device: BluetoothDevice) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device)
        }
    }

    fun getDevice(position: Int): BluetoothDevice {
        return mLeDevices[position]
    }

    fun clear() {
        mLeDevices.clear()
    }

    override fun getCount(): Int {
        return mLeDevices.size
    }

    override fun getItem(i: Int): Any {
        return mLeDevices[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View? {
        inflater = LayoutInflater.from(context)
        var view = view
        val viewHolder: ViewHolder
        // General ListView optimization code.
        if (view == null) {
            view = inflater.inflate(R.layout.device, null)
            viewHolder = ViewHolder()
            viewHolder.deviceAddress = view.findViewById(R.id.address)
            viewHolder.deviceName = view.findViewById(R.id.name)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val device = mLeDevices[i]
        val deviceName = device.name
        if (deviceName != null && deviceName.isNotEmpty())
            viewHolder.deviceName?.setText(deviceName)
        else
            viewHolder.deviceName?.text = "unknown device"
        viewHolder.deviceAddress?.text = device.address

        return view
    }
}

internal class ViewHolder {
    var deviceName: TextView? = null
    var deviceAddress: TextView? = null
}