package id.rakaadinugroho.midtransintegration

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.LocalDataHandler
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillInfoModel
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.UserDetail
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.midtrans.sdk.corekit.models.UserAddress
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), TransactionFinishedCallback {

    override fun onTransactionFinished(transactionResult: TransactionResult) {
        when {
            transactionResult.response != null -> {
                when (transactionResult.status) {
                    TransactionResult.STATUS_SUCCESS -> {
                        Toast.makeText(this, "Success transaction", Toast.LENGTH_LONG).show()
                    }
                    TransactionResult.STATUS_PENDING -> {
                        Toast.makeText(this, "Pending transaction", Toast.LENGTH_LONG).show()
                    }
                    TransactionResult.STATUS_FAILED -> {
                        Toast.makeText(this, "Failed ${transactionResult.response.statusMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            transactionResult.isTransactionCanceled -> {
                Toast.makeText(this, "Canceled transaction", Toast.LENGTH_LONG).show()
            }
            else -> {
                if (transactionResult.status.equals(TransactionResult.STATUS_INVALID, true))
                    Toast.makeText(this, "Invalid transaction", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this, "Failure transaction", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list_product.check(first_product.id)

        /**
         * Initialization SDK UI from Midtrans
         */
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-UpdeLV8Hbq4_9iVe")
            .setContext(this)
            .setTransactionFinishedCallback(this)
            .setMerchantBaseUrl("https://ayokode.com/midtrans/checkout.php/")
            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .buildSDK()

        /**
         * Set Dummy User Customers
         */
        var userDetail: UserDetail? = LocalDataHandler.readObject("user_details", UserDetail::class.java)
        if (userDetail == null) {
            userDetail = UserDetail()
            userDetail.userFullName = "Raka Adi Nugroho"
            userDetail.email = "admin@blogtoandroid.com"
            userDetail.phoneNumber = "085601053150"
            userDetail.userId = "user-${System.currentTimeMillis()}"

            val userAddresses: ArrayList<UserAddress> = ArrayList()
            val userAddress = UserAddress()
            userAddress.address = "Jl Raya Merdeka Barat"
            userAddress.city = "Banjarnegara"
            userAddress.addressType = com.midtrans.sdk.corekit.core.Constants.ADDRESS_TYPE_BOTH
            userAddress.zipcode = "53456"
            userAddress.country = "IDN"
            userAddresses.add(userAddress)
            userDetail.userAddresses = userAddresses
            LocalDataHandler.saveObject("user_details", userDetail)
        }

        pay_now.setOnClickListener {
            when (list_product.checkedRadioButtonId) {
                first_product.id -> {
                    val itemDetailsList: ArrayList<ItemDetails> = ArrayList()
                    itemDetailsList.add(ItemDetails("1", 20000.0, 1, "20 Point Balance"))
                    setPaymentTotal(itemDetailsList, 20000.0)
                }
                second_product.id -> {
                    val itemDetailsList: ArrayList<ItemDetails> = ArrayList()
                    itemDetailsList.add(ItemDetails("2", 50000.0, 1, "100 Point Balance"))
                    setPaymentTotal(itemDetailsList, 50000.0)
                }
                third_product.id -> {
                    val itemDetailsList: ArrayList<ItemDetails> = ArrayList()
                    itemDetailsList.add(ItemDetails("3", 100000.0, 1, "1000 Point Balance"))
                    setPaymentTotal(itemDetailsList, 100000.0)
                }
            }

            MidtransSDK.getInstance().startPaymentUiFlow(this)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
    }

    /**
     * Setup Total detail cart
     */
    private fun setPaymentTotal(itemDetailsList: ArrayList<ItemDetails>, amount: Double) {
        val transactionRequest = TransactionRequest(System.currentTimeMillis().toString(), amount)
        transactionRequest.itemDetails = itemDetailsList
        val billInfoModel = BillInfoModel("demo_label", "demo_value")
        transactionRequest.billInfoModel = billInfoModel
        MidtransSDK.getInstance().transactionRequest = transactionRequest
    }
}
