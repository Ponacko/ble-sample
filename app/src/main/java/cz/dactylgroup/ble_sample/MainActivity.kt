package cz.dactylgroup.ble_sample

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import kotlinx.android.synthetic.main.activity_main.*
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private val leDeviceAdapter : LeDeviceListAdapter = LeDeviceListAdapter(this)
    private var mBluetoothGatt: BluetoothGatt? = null
    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, _, _ ->
        runOnUiThread {
            leDeviceAdapter.addDevice(device)
            leDeviceAdapter.notifyDataSetChanged()
        }
    }

    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int,
                                             newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("CONNECTION STATE", "CONNECTED")
                gatt.discoverServices()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val services = mBluetoothGatt!!.services

                val characteristic = services[2].characteristics[0]

                val result = writeCharacteristic(characteristic)
                Log.d("writeResult", result.toString())
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("readResult", characteristic?.getStringValue(0))
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d("writeResult", status.toString())
            }
            val result = readCharacteristic(characteristic)
            Log.d("readResult", result.toString())
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        if (!mBluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }

        scanDevices()
        list.adapter = leDeviceAdapter

        list.onItemClickListener = OnItemClickListener { adapterView, view, position, id ->
            val device = leDeviceAdapter.getItem(position) as BluetoothDevice
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback)
        }
    }

    fun findCharacteristic(): BluetoothGattCharacteristic? {
        if (mBluetoothGatt != null){
            mBluetoothGatt!!.services
                    .map { it.characteristics }
                    .forEach { return it.find { it.uuid == UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb") } }
        }
        return null
    }

    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic?): Boolean {
        return if (mBluetoothGatt != null) {
            characteristic?.value = "W200200".toByteArray()
            mBluetoothGatt!!.writeCharacteristic(characteristic)
        } else false
    }

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic?): Boolean {
        return if (mBluetoothGatt != null) {
            mBluetoothGatt!!.readCharacteristic(characteristic)
        } else false
    }

    fun scanDevices() {
        val mHandler = Handler()
        val  SCAN_PERIOD = 10000L
        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed({
            mBluetoothAdapter.stopLeScan(mLeScanCallback)
        }, SCAN_PERIOD)

        mBluetoothAdapter.startLeScan(mLeScanCallback)
    }
}
