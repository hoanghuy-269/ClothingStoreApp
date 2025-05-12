package com.example.clothingstoreapp.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.address.model.City
import com.example.clothingstoreapp.ModelAPI.CityAPI
import com.example.clothingstoreapp.databinding.AddressLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import kotlin.collections.ArrayList

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: AddressLayoutBinding
    private var cityList = arrayListOf<City>()
    private var distriList = arrayListOf<City.District>()
    private var wardList = arrayListOf<City.Ward>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddressLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // tạo retrofix
        val retrofit = Retrofit.Builder()
            .baseUrl(CityAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val cityAPI = retrofit.create(CityAPI::class.java)
        loadCities(cityAPI)
        setContent()

    }
    private fun setContent()
    {
     binding.btnCapNhat.setOnClickListener {
         val ward = binding.spinnerWard.selectedItem?.toString()?:" "
         val district = binding.spinnerDistrict.selectedItem?.toString()?:" "
         val city = binding.spinnerCity.selectedItem?.toString()?:" "
         val detail = binding.edtDetailAddress.text.toString().trim()
         val fullAdress = "$ward , $district, $city ,$detail"
         val ketqua = Intent()
         ketqua.putExtra("selectedAddress",fullAdress)
         ketqua.putExtra("detailAddress",detail)
         setResult(Activity.RESULT_OK,ketqua)
         finish()
     }
    }


    private fun loadCities(cityAPI: CityAPI)
    {
        val call = cityAPI.getAllCities()
        call.enqueue(object :Callback<ArrayList<City>>{
            override fun onResponse(p0: Call<ArrayList<City>>, response: Response<ArrayList<City>>) {
                if(response.isSuccessful)
                {
                    cityList = response.body() ?: arrayListOf()
                    setupCitySpinner()                }
                else
                {
                    Toast.makeText(this@AddressActivity, "load districts thaas bại", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(p0: Call<ArrayList<City>>, t: Throwable) {
                Toast.makeText(this@AddressActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun setupCitySpinner() {
        val cityNames = cityList.map { it.name }
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cityNames)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = cityAdapter

        binding.spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Lấy mã thành phố khi chọn thành phố
                val selectedCityCode = cityList[position].code
                loadDistricts(selectedCityCode)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }
    }
    private fun loadDistricts(cityCode: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(CityAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val cityAPI = retrofit.create(CityAPI::class.java)

        val call = cityAPI.getCityBycode(cityCode)
        call.enqueue(object : Callback<City> {
            override fun onResponse(call: Call<City>, response: Response<City>) {
                if (response.isSuccessful) {
                    distriList = response.body()?.districts ?: arrayListOf()
                    setupDistriSpinner()
                } else {
                    Toast.makeText(this@AddressActivity, "Failed to load districts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<City>, t: Throwable) {
                Toast.makeText(this@AddressActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupDistriSpinner()
    {
        var districtName = distriList.map { it.name }
        val districtAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,districtName)
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDistrict.adapter = districtAdapter

        binding.spinnerDistrict.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedDistrict = distriList[position].code
                loadWards(selectedDistrict)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }
    private fun loadWards(districtCode:Int)
    {
        val retrofit = Retrofit.Builder()
            .baseUrl(CityAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val cityAPI = retrofit.create(CityAPI::class.java)
        val call = cityAPI.getDistriByCode(districtCode)
        call.enqueue(object :Callback<City.District>{
            override fun onResponse(p0: Call<City.District>, response: Response<City.District>) {
                if(response.isSuccessful)
                {
                    wardList = response.body()?.wards?: arrayListOf()
                    setupWardSpinner()
                }else
                {
                    Toast.makeText(this@AddressActivity,"load that bai",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<City.District>, t: Throwable) {
                Toast.makeText(this@AddressActivity,"Lỗi ${t.message} ",Toast.LENGTH_SHORT).show()

            }

        })
    }

    private fun setupWardSpinner()
    {
        val wardNames = wardList.map { it.name }
        val wardAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,wardNames)
        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWard.adapter = wardAdapter
    }
}