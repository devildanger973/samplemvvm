package paint

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R

class ColorPickerAdapter(
    private val context: Context,
    private val listener: OnColorPickerClickListener,
) :
    RecyclerView.Adapter<ColorPickerAdapter.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val colorPickerColors: List<Int>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.color_picker_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemColor = colorPickerColors[position]
        Glide.with(context).load(itemColor)
            .into(holder.colorPickerView as ImageView)
        holder.colorPickerView.setBackgroundColor(colorPickerColors[position])
        holder.bind(item = itemColor, position)

    }

    override fun getItemCount(): Int {
        return colorPickerColors.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         *
         */
        var colorPickerView: View = itemView.findViewById(R.id.color_picker_view)

        fun bind(item: Int, position: Int) {
            colorPickerView.setOnClickListener {
                listener.onColorPickerClickListener(item)
            }
        }
    }

    /**
     *
     */
    interface OnColorPickerClickListener {
        /**
         *
         */
        fun onColorPickerClickListener(colorCode: Int)
    }

    private fun getKelly22Colors(context: Context): List<Int> {
        val resources = context.resources
        val colorList: MutableList<Int> = ArrayList()
        for (i in 0..21) {
            val resourceId =
                resources.getIdentifier("kelly_" + (i + 1), "color", context.packageName)
            colorList.add(resources.getColor(resourceId))
        }
        return colorList
    }

    init {
        colorPickerColors = getKelly22Colors(context)
    }
}

