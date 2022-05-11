package paint

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.ItemSelectedAdapter
import com.example.myapplication.R

class ColorPickerAdapter(
    private val mContext: Context,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
    private val colorPickerColors: MutableList<Int>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.color_picker_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return colorPickerColors.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         *
         */
        var colorPickerView: ImageView = itemView.findViewById(R.id.color_picker_view)
        fun bind(item: Int, position: Int) {
            listener.onItemClick(item)
        }


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
        colorPickerColors = getKelly22Colors(mContext).toMutableList()
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val color = colorPickerColors[position]
        Glide.with(mContext).load(color)
            .into((holder as ColorPickerAdapter.ViewHolder).colorPickerView)
        holder.colorPickerView.setBackgroundColor(colorPickerColors[position])
        holder.bind(item = color, position)

    }

    /**
     *
     */
    interface OnItemClickListener {
        /**
         *
         */
        fun onItemClick(item: Int)
    }
}
