package com.sandy.seoul_matcheap.ui.more.info

import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.databinding.FragmentLicenseBinding
import com.sandy.seoul_matcheap.ui.common.BaseFragment

class LicenseFragment : BaseFragment<FragmentLicenseBinding>(R.layout.fragment_license) {

    override fun setupBinding(): FragmentLicenseBinding {
        return binding
    }

    override fun initView() = binding.run {
        btnBack.setOnClickListener { setOnBackPressedListener() }
        webView.loadUrl(LICENSE_URI)
    }

    companion object {
        private const val LICENSE_URI = "file:///android_asset/license.html"
    }

}