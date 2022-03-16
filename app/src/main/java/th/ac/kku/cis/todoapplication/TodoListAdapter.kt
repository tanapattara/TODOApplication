package th.ac.kku.cis.todoapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.LayoutInflaterFactory

class TodoListAdapter(context: Context, todoItemList: MutableList<Todo>) : BaseAdapter(){
    var itemList = todoItemList
    var mInference: LayoutInflater = LayoutInflater.from(context)
    var rowListener: ListViewListener = context as ListViewListener
    override fun getCount(): Int {
        return  itemList.count()
    }
    override fun getItem(p0: Int): Any {
        return itemList.get(p0)
    }
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var objID: String = itemList.get(p0).objectId as String
        var itemText: String = itemList.get(p0).todoText as String
        var itemDone: Boolean = itemList.get(p0).done as Boolean

        var view: View
        var listItemHolder : ListItemHolder
        if(p1 == null){
            view = mInference.inflate(R.layout.item_todo, p2, false)
            listItemHolder = ListItemHolder(view)
            view.tag = listItemHolder
        }else {
            view = p1
            listItemHolder = view.tag as ListItemHolder
        }
        listItemHolder.label.text = itemText
        listItemHolder.checkBox.isChecked = itemDone

        //add listener event
        listItemHolder.checkBox.setOnClickListener{
            rowListener.onUpdateItem(objID, p0, !itemDone)
        }
        listItemHolder.buttonDelete.setOnClickListener{
            rowListener.onDeleteItem(objID, p0)
        }

        return view
    }

    private class  ListItemHolder(row: View?){
        var label: TextView = row!!.findViewById(R.id.textView)
        var checkBox: CheckBox = row!!.findViewById(R.id.checkBox_status)
        var buttonDelete: Button = row!!.findViewById(R.id.button_delete)
    }
}