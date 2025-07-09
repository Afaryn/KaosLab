//package com.example.keazy.utils
//
//import android.app.AlertDialog
//import android.app.Dialog
//import android.content.Context
//import android.os.Bundle
//import android.view.View
//import android.widget.AdapterView
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageButton
//import android.widget.LinearLayout
//import android.widget.Spinner
//import androidx.fragment.app.DialogFragment
//import com.example.keazy.R
//import com.example.keazy.model.Filter
//import com.example.keazy.views.treatment.TreatmentActivity
//import java.util.logging.Filter
//
//class FilterDialogFragment : DialogFragment() {
//    private lateinit var filtersContainer: LinearLayout
//    private lateinit var btnAddFilter: Button
//    private lateinit var btnApply: Button
//    private lateinit var btnCancel: Button
//
//    private val filterList = mutableListOf<Filter>()
//    private val textOperators = listOf("Contains", "Does not contain", "Is empty", "Is not empty")
//    private val numberOperators = listOf("Less than", "Greater than", "Equal to", "Not equal to", "Less than or equal to", "Greater than or equal to")
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val builder = AlertDialog.Builder(requireContext())
//        val inflater = requireActivity().layoutInflater
//        val view = inflater.inflate(R.layout.dialog_filter, null)
//
//        filtersContainer = view.findViewById(R.id.filtersContainer)
//        btnAddFilter = view.findViewById(R.id.btnAddFilter)
//        btnApply = view.findViewById(R.id.btnApply)
//        btnCancel = view.findViewById(R.id.btnCancel)
//
//        btnAddFilter.setOnClickListener { addFilterItem() }
//        btnApply.setOnClickListener { applyFilters() }
//        btnCancel.setOnClickListener { dismiss() }
//
//        addFilterItem()
//
//        builder.setView(view)
//        return builder.create()
//    }
//
//    interface FilterDialogListener {
//        fun onFiltersApplied(filters: List<Filter>)
//    }
//
//    private var listener: FilterDialogListener? = null
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        listener = context as? FilterDialogListener
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }
//
//    private fun addFilterItem() {
//        val filterView = layoutInflater.inflate(R.layout.item_filter, filtersContainer, false)
//        val spinnerField = filterView.findViewById<Spinner>(R.id.spinnerField)
//        val spinnerOperator = filterView.findViewById<Spinner>(R.id.spinnerOperator)
//        val editTextValue = filterView.findViewById<EditText>(R.id.editTextValue)
//        val btnDeleteFilter: ImageButton = filterView.findViewById(R.id.btnDeleteFilter)
//
//        val fieldsAdapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_item,
//            resources.getStringArray(R.array.filter_fields)
//        )
//        fieldsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerField.adapter = fieldsAdapter
//
//        val operatorAdapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_item,
//            textOperators
//        )
//        operatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerOperator.adapter = operatorAdapter
//
//        spinnerField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                val selectedField = spinnerField.selectedItem.toString()
//                val newOperators = if (selectedField == "Price" || selectedField == "Duration") {
//                    numberOperators
//                } else {
//                    textOperators
//                }
//                spinnerOperator.adapter = ArrayAdapter(
//                    requireContext(),
//                    android.R.layout.simple_spinner_item,
//                    newOperators
//                ).apply {
//                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {}
//        }
//
//        spinnerOperator.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                val selectedOperator = spinnerOperator.selectedItem.toString()
//                editTextValue.inputType = if (selectedOperator in numberOperators) {
//                    android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
//                } else {
//                    android.text.InputType.TYPE_CLASS_TEXT
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {}
//        }
//
//        btnDeleteFilter.setOnClickListener {
//            filtersContainer.removeView(filterView)
//        }
//
//        filtersContainer.addView(filterView)
//    }
//
//    private fun applyFilters() {
//        val filters = mutableListOf<Filter>()
//
//        for (i in 0 until filtersContainer.childCount) {
//            val view = filtersContainer.getChildAt(i)
//            val field = view.findViewById<Spinner>(R.id.spinnerField).selectedItem.toString()
//            val operator = view.findViewById<Spinner>(R.id.spinnerOperator).selectedItem.toString()
//            val value = view.findViewById<EditText>(R.id.editTextValue).text.toString()
//            filters.add(Filter(field, operator, value))
//        }
//
//        (requireActivity() as TreatmentActivity).viewModel.applyFilters(filters)
//        dismiss()
//    }
//}
