package com.sandy.seoul_matcheap.ui.more.info

import android.content.Intent
import android.net.Uri
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.sandy.seoul_matcheap.BuildConfig
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.databinding.FragmentInfoBinding
import com.sandy.seoul_matcheap.ui.common.BaseFragment
import com.sandy.seoul_matcheap.util.constants.ACTION_SEND_TYPE
import com.sandy.seoul_matcheap.util.constants.MAIL_TO_DEVELOPER_ADDRESS

class InfoFragment : BaseFragment<FragmentInfoBinding>(R.layout.fragment_info) {

    override fun setupBinding(): FragmentInfoBinding {
        return binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@InfoFragment
        }
    }

    override fun initView() = binding.run {
        tvVersion.setVersionName()
        btnBack.setOnBackButtonClickListener()
    }

    private fun TextView.setVersionName() {
        text = BuildConfig.VERSION_NAME
    }

    fun navigateToOpenLicenseDetails() {
        findNavController().navigate(R.id.action_infoFragment_to_licenseFragment)
    }

    fun sandEmailToDeveloper() = Intent(Intent.ACTION_SENDTO).apply {
        type = ACTION_SEND_TYPE
        data = Uri.parse(MAIL_TO_DEVELOPER_ADDRESS)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }.run(::startActivity)

}