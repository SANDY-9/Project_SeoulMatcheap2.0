package com.sandy.seoul_matcheap.ui.details

import android.Manifest
import android.app.Activity
import android.content.*
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.sandy.matcheap.common.*
import com.sandy.matcheap.domain.model.store.StoreDetails
import com.sandy.seoul_matcheap.MatcheapApplication.Companion.showToastMessage
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.adapters.StoreMenuAdapter
import com.sandy.seoul_matcheap.databinding.ActivityStoreDetailsBinding
import com.sandy.seoul_matcheap.util.custom.TouchFrameLayout
import com.sandy.seoul_matcheap.ui.more.bookmark.BookmarkViewModel
import com.sandy.seoul_matcheap.util.constants.*
import com.sandy.seoul_matcheap.util.helper.MapUtils
import com.sandy.seoul_matcheap.util.helper.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import setStatusBarState
import javax.inject.Inject


@AndroidEntryPoint
class StoreDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreDetailsBinding

    private val storeDetailsViewModel: StoreDetailsViewModel by viewModels()
    private val bookmarkViewModel: BookmarkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerOnBackPressedDispatcher()
        setBinding()
        handleIntent(intent)
        initView()
        subscribeToObservers()
    }

    private fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_store_details)
        with(binding) {
            lifecycleOwner = this@StoreDetailsActivity
            activity = this@StoreDetailsActivity
        }
    }

    private fun handleIntent(intent: Intent) {
        intent.getStringExtra(STORE_ID)?.let {
            updateStoreDetails(it)
            return
        }
        handleFirebaseDynamicLink(intent)
    }

    private fun updateStoreDetails(id: String) {
        storeDetailsViewModel.updateStoreDetails(id)
    }

    private fun handleFirebaseDynamicLink(intent: Intent) = Firebase.dynamicLinks
        .getDynamicLink(intent)
        .addOnSuccessListener(this) { dynamicLinkData: PendingDynamicLinkData? ->
            dynamicLinkData?.link?.getQueryParameter(STORE_ID)?.let {
                updateStoreDetails(it)
            }
        }
        .addOnFailureListener(this) {
            showToastMessage(this, MESSAGE_NETWORK_ERROR)
            onFinish()
        }


    private fun initView() = binding.run {
        //풀스크린 모드 조절
        setStatusBarState(true)
        constraintLayout.setTransitionListener(
            object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(layout: MotionLayout?, start: Int, end: Int) { /* NO-OP */ }
                override fun onTransitionCompleted(layout: MotionLayout?, current: Int) { /* NO-OP */ }
                override fun onTransitionTrigger(layout: MotionLayout?, trigger: Int, positive: Boolean, progress: Float) { /* NO-OP */ }
                override fun onTransitionChange(layout: MotionLayout?, start: Int, end: Int, progress: Float) {
                    setStatusBarState(progress < STANDARD_POSITION)
                }
            }
        )

        contentView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            binding.tvTitle.alpha = if(scrollY > 200) VISIBLE else GONE
        }

        mapContainer.setTouchListener(
            object : TouchFrameLayout.OnTouchListener {
                override fun onTouch() {
                    binding.contentView.requestDisallowInterceptTouchEvent(true)
                }
            }
        )
    }

    private fun subscribeToObservers() {
        storeDetailsViewModel.storeDetailsState.observe(this) {
            handleStoreDetailsState(it)
        }
        storeDetailsViewModel.storeMenuListState.observe(this) {
            handleStoreMenuState(it)
        }
    }

    private fun handleStoreDetailsState(state: StoreDetailsState) {
        val storeDetails = state.data
        binding.storeDetails = state.data
        setupMap(storeDetails)
    }

    @Inject lateinit var mapUtils: MapUtils
    private fun setupMap(storeDetails: StoreDetails?) = storeDetails?.let{ store ->
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync { map ->
            mapUtils.createStoreLocationMarker(storeDetails, map)

            val latLng = LatLng(store.lat, store.lng)
            CameraPosition(latLng, MapUtils.MAP_DEFAULT_ZOOM).let { position ->
                map.cameraPosition = position

                // GPS 버튼을 누르면 카메라 중심좌표를 store의 위치로 옮긴다.
                binding.btnGps.setOnClickListener { map.cameraPosition = position }
            }
        }
    }

    private fun handleStoreMenuState(state: StoreMenuListState) {
        binding.rvMenu.adapter = StoreMenuAdapter().apply {
            submitList(state.data)
        }
    }


    @Inject lateinit var connectivityManager: ConnectivityManager
    fun updateBookmarkState(v: View) {
        val isNetworkEnabled = connectivityManager.activeNetwork
        isNetworkEnabled?.let {
            val curBookmark = v.isSelected
            v.isSelected = !curBookmark
        } ?: showToastMessage(this, MESSAGE_NETWORK_ERROR)
    }

    override fun onStop() {
        super.onStop()
        updateBookmarkChanged()
    }

    private fun updateBookmarkChanged() {
        val storeDetails = binding.storeDetails
        storeDetails?.run {
            val bookmarkBtnChecked = binding.btnBookmark.isSelected
            if (bookmarkBtnChecked != bookmark?.bookmarked) {
                bookmarkViewModel.updateBookmark(store.id, store.code, bookmarkBtnChecked)
            }
        }
    }


    fun startSendToIntent(store: StoreDetails) = Intent(Intent.ACTION_SENDTO).apply {
        val uriText = MAIL_TO_DEVELOPER_ADDRESS +
                "?subject=" + Uri.encode(EMAIL_TITLE) +
                "&body=" + Uri.encode(getEmailContent(store))
        data = Uri.parse(uriText)
    }.run(::startActivity)
    private fun getEmailContent(store: StoreDetails) = """
       --------<착한가격업소 정보>----------
       가게 ID : ${store.id}
       가게 이름 : ${store.name}
       -------------------------------------------------
        
       수정 제안할 내용을 이곳에 입력해주세요.
       (사진 교체를 희망 하시는 경우, 메일에 사진도 같이 첨부해서 보내주셔야 합니다.)
    """.trimIndent()


    fun requestSandToOtherApp(store: StoreDetails) {
        createDeepLink(store)
    }
    private fun createDeepLink(store: StoreDetails) = Firebase.dynamicLinks.shortLinkAsync {
        link = Uri.parse(DEEP_LINK_URI_PREFIX + store.id)
        domainUriPrefix = DOMAIN_URI_PREFIX
        androidParameters(packageName) { }
        socialMetaTagParameters {
            title = "착한가격업소 [${store.name}]"
            description = DYNAMIC_LINK_DESC
            imageUrl = Uri.parse(store.photo)
        }
    }.addOnSuccessListener { (shortLink, _) ->
        shortLink?.let {
            startChooserIntent(store, shortLink)
        }
    }.addOnFailureListener {
        showToastMessage(this, MESSAGE_WARNING_SHARE)
    }

    private fun startChooserIntent(store: StoreDetails, shortLink: Uri) = Intent.createChooser(
        Intent(Intent.ACTION_SEND).apply {
            type = ACTION_SEND_TYPE
            putExtra(Intent.EXTRA_TEXT, getSharedContent(store, shortLink))
        },
        APP_NAME
    ).run(::startActivity)
    private fun getSharedContent(store: StoreDetails, link: Uri) =
        """
            [${store.name}]
            
            ${binding.content.text}
            
            주소 : ${store.address}
            
            $link
            """.trimIndent()


    fun requestCall() = callPermissionRequest.launch(Manifest.permission.CALL_PHONE)
    private val callPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> handleCallPermissionResult(isGranted) }
    private fun handleCallPermissionResult(isGranted: Boolean) = when {
        isGranted -> startActionDialIntent()
        else -> hasNotGrantedCallPermission()
    }

    private fun startActionDialIntent() {
        val callNumber = binding.storeDetails.tel
        callNumber?.let {
            Intent(Intent.ACTION_DIAL).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                data = Uri.parse(it)
            }.run(::startActivity)
        }
    }

    private fun hasNotGrantedCallPermission() {
        PermissionHelper.getPermissionSettingsIntent(this)
        showToastMessage(this, MESSAGE_PERMISSION_WARNING_CALL)
    }


    fun requestNaviApp(store: StoreDetails) {
        startNavigationIntent(store)
    }
    private fun startNavigationIntent(store: StoreDetails) = try {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(getNavigationUriString(store))
            `package` = NAVER_MAP_PACKAGE_NAME
        }.run(::startActivity)
    } catch (e : Exception) {
        showToastMessage(this, MESSAGE_APP_WARNING)
    }

    private fun getNavigationUriString(store: StoreDetails) =
        NAVER_MAP_URI + "dlat=${store.lat}" + "&dlng=${store.lng}" + "&dname=${store.name}" + "&appname=${packageName}"

    private fun registerOnBackPressedDispatcher() = onBackPressedDispatcher.addCallback(this,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onFinish()
            }
        }
    )

    fun onFinish() {
        binding.storeDetails?.run {
            val item = StoreDetails(
                id = id,
                code = code,
                gu = gu,
                name = name,
                address = address,
                photo = photo,
                lat = lat,
                lng = lng,
                bookmarked = binding.btnBookmark.isSelected
            )
            val returnIntent = Intent().apply {
                putExtra(STORE_, item)
            }
            setResult(Activity.RESULT_OK, returnIntent)
        }
        finish()
    }

    companion object {
        private const val STANDARD_POSITION = 0.5
        private const val EMAIL_TITLE = "[서울맛칩] 가게 정보 수정 요청"
        private const val DYNAMIC_LINK_DESC = "서울맛칩 앱에서 보기"
    }

}