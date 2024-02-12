package com.sandy.seoul_matcheap.ui.common

import android.content.Intent
import android.net.Uri
import android.os.*
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.*
import androidx.fragment.app.Fragment
import androidx.navigation.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sandy.seoul_matcheap.MatcheapApplication.Companion.showToastMessage
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.ui.store.StoreDetailsActivity
import com.sandy.seoul_matcheap.util.constants.*
import com.sandy.seoul_matcheap.util.helper.PermissionHelper

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-21
 * @desc
 */

abstract class BaseFragment<B: ViewDataBinding>(@LayoutRes private val layoutId: Int) : Fragment() {

    private var _binding: B? = null
    protected val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        registerOnBackPressedDispatcher()
        setBinding(inflater, container)
        return binding.root
    }

    private fun setBinding(inflater: LayoutInflater, container: ViewGroup?) {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        setupBinding()
        downloadData()
    }

    /**
     * 바인딩 객체의 binding value(viewModel) 등등을 설정 해줘야 할 때 or lifecycle Owner의 변경이 필요할 때
     * 해당 함수를 override 합니다(default lifecycle owner는 viewLifecycleOwner).
     * 이후에 데이터 업데이트가 필요하면 downloadData()를 override 합니다. */
    abstract fun setupBinding() : B

    /**
     * 바인딩 객체를 초기화한 후, 실행되는 함수.
     * onCreateView에서 root view를 리턴하기 전에 뷰에 업데이트할 데이터를 다운받아야 한다면 이 함수를 override합니다. */
    protected open fun downloadData() = Unit


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGlobalVariables()
        initView()
        subscribeToObservers()
    }

    /**
     * memory leaks을 관리하기 위해 리사이클러뷰 어댑터 객체는 nullable global variable로 field에 선언해야 한다.
     * 프래그먼트 view가 destroy 될 때, 뷰의 라이프사이클보다 오래 존재하지 않도록 null처리 해줘야 하기 때문이다.
     * 어댑터 객체는 뷰가 생성된 후(onViewCreated) 이 함수를 사용해서 초기화 한다.
     */
    protected open fun initGlobalVariables() = Unit

    /** 뷰가 생성된 후(onViewCreated) view components 객체들을 초기화한다. */
    abstract fun initView()


    //!-- 여기서부터 재사용이 빈번한 뷰 확장함수

    protected fun RecyclerView.addAdapter(recyclerViewAdapter: Adapter<out ViewHolder>?) {
        adapter = recyclerViewAdapter
    }

    protected open fun Adapter<out ViewHolder>.addOnItemClickListener() = Unit

    protected fun TextView.setVisibleForScroll(y: Int) {
        visibility = if(y > MIN_SCROLL) View.VISIBLE else View.GONE
    }

    protected fun ImageView.setOnBackButtonClickListener() = setOnClickListener { setOnBackPressedListener() }

    protected fun View.changeScale(scale: Float = SCALE) {
        scaleX = scale
        scaleY = scale
    }

    protected fun TextView.setChangeTextColorOnTouch(action: Int) {
        val color = when (action) {
            MotionEvent.ACTION_UP -> Resource.matCheapGray
            else -> Resource.matCheapBlue
        }
        setTextColor(color)
    }

    protected fun MotionLayout.startTransition(id: Int) {
        setTransition(id)
        transitionToEnd()
    }


    protected open fun subscribeToObservers() = Unit

    protected open fun TextView.setOnRetryButtonClickListener() = Unit


    //!-- 퍼미션 관련
    private val permissionSettingsIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        handlePermissionSettingsActivityResult()
    }
    protected open fun handlePermissionSettingsActivityResult() = Unit
    protected fun startPermissionSettingsIntent() {
        val intent = PermissionHelper.getPermissionSettingsIntent(requireContext())
        permissionSettingsIntentLauncher.launch(intent)
    }

    protected fun registerHandler(handler: Handler = Handler(Looper.getMainLooper()), delay: Long, func: () -> Unit) {
        handler.postDelayed(func, delay)
    }


    /**
     * StoreDetailsActivity로 화면을 이동한다. StoreDetailsActivity로 이동할 때, store ID를 반드시 intent에 담아서 같이 이동해야한다.
     * @param id store ID. "0000"으로 시작한다.
     */
    open fun navigateToStoreDetails(id: String) {
        Intent(requireContext(), StoreDetailsActivity::class.java).apply {
            putExtra(STORE_ID, id)
        }.run(::startActivity)
    }

    fun navigateToBrowser(url: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).run(::startActivity)
    }

    protected fun navigateDirections(action: NavDirections) {
        findNavController().navigate(action)
    }

    protected fun getPopUpNavOptions() = NavOptions.Builder().setPopUpTo(R.id.nav_graph, false).build()

    private fun registerOnBackPressedDispatcher() = requireActivity().onBackPressedDispatcher
        .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setOnBackPressedListener()
            }
        })

    /** onBackPressed의 callback으로 동작되는 함수 */
    open fun setOnBackPressedListener() {
        findNavController().popBackStack()
    }

    protected fun handleExistDeepLinkNavigation() {
        findNavController().run {
            previousBackStackEntry?.let {
                popBackStack()
                return
            }
            navigate(R.id.homeFragment, null, getPopUpNavOptions())
        }
    }


    override fun onDestroyView() {
        //memory leaks을 막기 위해 반드시 해줘야 하는 설정들
        _binding = null
        destroyGlobalVariables()
        super.onDestroyView()
    }

    /**
     * global field들 중 일부(ex. 리사이클러뷰 어댑터)는 프래그먼트가 onDestroyView된 이후에도 존재해서 memory leaks을 유발하므로
     * 이를 막기 위해 onDestroyView될 때, global field들도 같이 destroy해줘야 한다.
     */
    protected open fun destroyGlobalVariables() = Unit

}
