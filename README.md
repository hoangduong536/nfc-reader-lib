📦 Sử dụng thư viện trong ứng dụng khác
**1.Thêm dependency vào build.gradle của ứng dụng:**

**implementation 'com.github.duong-futa:nfc-reader-lib:1.0.0'**

Lưu ý: Đảm bảo bạn đã cấu hình JitPack trong build.gradle của project:
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

Sử dụng trong Activity:


class MainActivity : AppCompatActivity(), NfcCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NfcReader.init(this, this)
    }

    override fun onResume() {
        super.onResume()
        NfcReader.enableForegroundDispatch(this)
    }

    override fun onPause() {
        super.onPause()
        NfcReader.disableForegroundDispatch(this)
    }

    override fun onProcessing() {
        Log.d("NFC", "Đang xử lý NFC...")
    }

    override fun onSuccess(tagData: String) {
        Log.d("NFC", "Đọc thành công: $tagData")
    }

    override fun onFailure(errorMessage: String) {
        Log.e("NFC", "Lỗi: $errorMessage")
    }
}
