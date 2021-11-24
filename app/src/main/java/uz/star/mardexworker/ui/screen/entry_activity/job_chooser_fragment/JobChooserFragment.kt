package uz.star.mardexworker.ui.screen.entry_activity.job_chooser_fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import uz.star.mardexworker.R
import uz.star.mardexworker.data.local.storage.LocalStorage
import uz.star.mardexworker.databinding.FragmentJobChooserBinding
import uz.star.mardexworker.model.response.jobs_data.EditJobsDeliverData
import uz.star.mardexworker.model.response.jobs_data.JobCategory
import uz.star.mardexworker.model.response.jobs_data.JobData
import uz.star.mardexworker.model.response.jobs_data.UserJobsDeliver
import uz.star.mardexworker.model.response.local.MessageData
import uz.star.mardexworker.model.response.login.AuthResponseData
import uz.star.mardexworker.ui.adapter.recycler_view.JobCategoryRVAdapter
import uz.star.mardexworker.ui.adapter.recycler_view.JobItemRVAdapter
import uz.star.mardexworker.ui.screen.main_activity.MainActivity
import uz.star.mardexworker.ui.screen.main_activity.profile_fragment.ProfileFragment.Companion.EDIT_PROFILE
import uz.star.mardexworker.utils.LANG_KRILL
import uz.star.mardexworker.utils.LANG_UZ
import uz.star.mardexworker.utils.extensions.*
import uz.star.mardexworker.utils.helpers.showAlertDialog
import uz.star.mardexworker.utils.views.hideKeyboard
import uz.star.mardexworker.utils.views.showView
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created by Botirali Kozimov on 08-03-21
 **/

@AndroidEntryPoint
class JobChooserFragment : Fragment() {

    private var _binding: FragmentJobChooserBinding? = null
    private val binding: FragmentJobChooserBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val viewModel: JobChooserViewModel by viewModels()

    private val args: JobChooserFragmentArgs by navArgs()
    private var allData: EditJobsDeliverData? = null
    private var userJobsData: UserJobsDeliver? = null

    @Inject
    lateinit var storage: LocalStorage

    private lateinit var adapterCategory: JobCategoryRVAdapter
    private lateinit var adapterJobItems: JobItemRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobChooserBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadViews()
        loadObservers()
    }

    private fun loadViews() {
        try {
            hideBottomMenu()
        } catch (e: Exception) {

        }
        args.userJobs?.let {
            it.ls.forEach {
                it.isSelected = true
            }
        }
        binding.apply {
            adapterCategory = JobCategoryRVAdapter(storage.language)
            adapterJobItems = JobItemRVAdapter(storage.language)

            listJobs.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = this@JobChooserFragment.adapterJobItems
            }

            listCategory.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = this@JobChooserFragment.adapterCategory
            }

            adapterCategory.setOnCategoryItemClickListener { it, title ->
                textJobs.text = getString(R.string.choose_jobs, title)
                if (args.jobsList != null && args.userJobs != null) {
                    userJobsData?.ls?.let { it1 -> adapterJobItems.submitListForUpdate(it, it1) }
                } else {
                    adapterJobItems.submitList(it)
                }
            }

            btnShowAll.setOnClickListener {
                if (listCategory.layoutManager is GridLayoutManager) {
                    listCategory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    listCategory.scrollToPosition(adapterCategory.lastSelectedIndex)
                    imageLine.rotation = 0f
                    textShowAll.setText(R.string.show_all)
                } else {
                    listCategory.layoutManager = GridLayoutManager(requireContext(), 2)
                    imageLine.rotation = 180f
                    textShowAll.setText(R.string.hide)
                }
            }

            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            btnSave.setOnClickListener {
                val ls = arrayListOf<String>()
                adapterCategory.currentList.forEach {
                    it?.jobs?.forEach { job ->
                        if (job.isSelected)
                            ls.add(job._id)
                    }
                }
                if (ls.size > 5)
                    showAlert()
                else
                    viewModel.addJobs(ls = ls)
            }

            if (args.jobsList != null && args.userJobs != null) {
                binding.textTitle.text = getString(R.string.my_jobs)
                allData = args.jobsList
                userJobsData = args.userJobs

                adapterCategory.submitList(allData?.ls)
                allData?.ls?.get(0)?.apply {
                    selected = true
                    userJobsData?.ls?.let { it1 -> adapterJobItems.submitListForUpdate(this.jobs, it1) }
                    val dataTitle = this.categoryTitle
                    val title =
                        if (storage.language == LANG_UZ) dataTitle.uz else if (storage.language == LANG_KRILL) dataTitle.uz_kr else dataTitle.ru
                    binding.textJobs.text = getString(R.string.choose_jobs, title)
                }
            } else {
                viewModel.getJobs()
            }

            adapterJobItems.setOnClickListener { d ->
                userJobsData?.let {
                    it.ls.forEach {
                        if (it._id == d._id) {
                            it.isSelected = false
                        }
                    }
                }
            }

            initializeSearchView()

            hideKeyboard(root)
        }
    }

    private fun initializeSearchView() {
        binding.apply {
            layoutSearch.setEndIconOnClickListener {
                hideKeyboard(root)
                search.text?.clear()

                showListJobs()
                showLayoutDef()
                textJobs.show()
                textSearchResults.hide()
                adapterJobItems.submitList(adapterCategory.currentList[adapterCategory.lastSelectedIndex].jobs)
            }

            search.doAfterTextChanged {
                val text = it.toString()
                if (text.trim().isNotEmpty()) {
                    searchJob(adapterCategory.currentList, text)
                } else {
                    searchJob(adapterCategory.currentList, "")
                    showLayoutDef()
                    textJobs.show()
                    textSearchResults.hide()
                    hideKeyboard(root)
                    adapterJobItems.submitList(adapterCategory.currentList[adapterCategory.lastSelectedIndex].jobs)
                }
            }
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        viewModel.jobs.observe(this, jobsObserver)
        viewModel.message.observe(this, messageObserver)
        viewModel.loader.observe(this, loaderObserver)
        viewModel.openHomeLiveData.observe(this, openHomeObserver)
    }

    private val loaderObserver = Observer<Boolean> { b ->
        if (allData == null)
            if (b) showEntryLoader() else hideEntryLoader()
        else
            if (b) showMainLoader() else hideMainLoader()
    }

    private val messageObserver = Observer<MessageData> { message ->
        message.onMessage {
            showMessage(it)
        }.onResource {
            showAlertDialog(it)
        }
    }

    private val jobsObserver = Observer<List<JobCategory>> { list ->
        list?.let {
            if (list.isNotEmpty()) {
                val data = list[0]
                data.selected = true
                if (args.jobsList != null && args.userJobs != null) {
                    userJobsData?.ls?.let { it1 -> adapterJobItems.submitListForUpdate(data.jobs, it1) }
                } else {
                    adapterJobItems.submitList(data.jobs)
                }
//                adapterJobItems.submitList(data.jobs)
                val dataTitle = data.categoryTitle
                val title =
                    if (storage.language == LANG_UZ) dataTitle.uz else if (storage.language == LANG_KRILL) dataTitle.uz_kr else dataTitle.ru
                binding.textJobs.text = getString(R.string.choose_jobs, title)
            }
        }
        adapterCategory.submitList(list)
    }

    private val openHomeObserver = Observer<AuthResponseData> { data ->
        if (allData == null) {
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        } else {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(EDIT_PROFILE, Bundle().apply {
                putBoolean(EDIT_PROFILE, true)
            })
            findNavController().popBackStack()
        }
    }

    private fun showMessage(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun searchJob(list: MutableList<JobCategory>, text: String) {
        binding.apply {
            emptyText.hide()
            showListJobs()
            if (text.trim().isEmpty()) {
                showLayoutDef()
                textJobs.show()
                textSearchResults.hide()
                return
            }
            textJobs.showView(false)
            textSearchResults.show()
            hideLayoutDef()
            val resultList = ArrayList<JobData>()
            for (jobCategory in list) {
                for (job in jobCategory.jobs) {
                    val dataTitle = job.title
                    val title =
                        if (storage.language == LANG_UZ) dataTitle.uz else if (storage.language == LANG_KRILL) dataTitle.uz_kr else dataTitle.ru
                    if (title?.toLowerCase(Locale.ROOT)
                            ?.contains(text.trim().toLowerCase(Locale.ROOT)) == true
                    ) {
                        resultList.add(job)
                    }
                }
            }
            adapterJobItems.submitList(resultList)
            if (resultList.isEmpty()) {
                hideListJobs()
                emptyText.show()
            }
        }
    }

    private fun showListJobs() {
        binding.apply {
            listJobs.show()
            textJobs.show()
        }
    }

    private fun hideListJobs() {
        binding.apply {
            listJobs.hide()
            textJobs.hide()
            textSearchResults.hide()
        }
    }

    private fun showLayoutDef() {
        binding.apply {
            /*if (listCategory.layoutManager is GridLayoutManager) {
                imageCloseCategory.show()
            } else
                imageCloseCategory.hide()*/
            textChooseCategory.show()
            listCategory.show()
            textShowAll.show()
            btnShowAll.show()
            imageLine.show()
        }
    }

    private fun hideLayoutDef() {
        binding.apply {
            /*if (listCategory.layoutManager is GridLayoutManager) {
                imageCloseCategory.hide()
            }*/
            textChooseCategory.hide()
            listCategory.hide()
            textShowAll.hide()
            btnShowAll.hide()
            imageLine.hide()
        }
    }

    private fun showAlert() {
        binding.root.context.apply {
            val dialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.warning_content))
                .setPositiveButton(getString(R.string.warning_ok)) { _, _ -> // continue with delete

                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create()
            dialog.setOnShowListener {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(binding.root.context, R.color.new_green))
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(binding.root.context, R.color.red))
            }
            dialog.show()
        }
    }
}