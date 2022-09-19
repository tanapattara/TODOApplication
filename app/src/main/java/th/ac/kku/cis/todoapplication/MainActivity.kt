package th.ac.kku.cis.todoapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), ListViewListener {
    lateinit var db: DatabaseReference
    lateinit var adapter: TodoListAdapter
    var listData = mutableListOf<Todo>()
    var itemListener: ValueEventListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            addDataToList(snapshot)
        }
        override fun onCancelled(error: DatabaseError) {
            Log.d("TODOACTIVITY", "loaditem, onCancelled event", error.toException())
        }
    }

    private fun addDataToList(snapshot: DataSnapshot) {
        var items = snapshot.children.iterator()
        if(items.hasNext()){
            var itemIndex = items.next()
            var itemsIterator = itemIndex.children.iterator()

            while (itemsIterator.hasNext()){
                var currentItem = itemsIterator.next()
                var map = currentItem.getValue() as HashMap<String, Any>

                var todoItem = Todo.create()
                todoItem.objectId = currentItem.key
                todoItem.todoText = map.get("todoText") as String
                todoItem.done = map.get("done") as Boolean
                listData.add(todoItem)
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseDatabase.getInstance().reference
        var listView_Todo:ListView = findViewById(R.id.list_todo)
        adapter = TodoListAdapter(this, listData)
        listView_Todo.adapter = adapter
        db.orderByKey().addListenerForSingleValueEvent(itemListener)

        var btn_new:FloatingActionButton = findViewById(R.id.btn_new)
        btn_new.setOnClickListener {
            var alertDialog = AlertDialog.Builder(this)
            var itemEditText = EditText(this)

            alertDialog.setTitle("Create new todo")
            alertDialog.setMessage("Enter your new todo task")
            alertDialog.setView(itemEditText)

            alertDialog.setPositiveButton("SAVE"){ dialog, positiveButton ->
                var newTodo = Todo.create()
                newTodo.todoText = itemEditText.text.toString()

                //create new data on firebase
                var newDbItem = db.child("todo_item").push()
                newTodo.objectId = newDbItem.key
                newDbItem.setValue(newTodo)

                dialog.dismiss()
                //add to local list
                listData.add(newTodo)
                adapter.notifyDataSetChanged()
            }
            alertDialog.show()

        }
    }

    override fun onUpdateItem(itemId: String, index: Int, isDone: Boolean) {
        Log.d("item.itemId ", "onUpdateItem")

        var item = db.child("todo_item").child(itemId)
        item.child("done").setValue(isDone)

        listData.get(index).done = isDone
        adapter.notifyDataSetChanged()
    }

    override fun onDeleteItem(itemId: String, index: Int) {
        Log.d("TODOAPPLICATION", "onDeleteItem")
        var item = db.child("todo_item").child(itemId)
        item.removeValue()

        listData.removeAt(index)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
        //return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_item_logout -> {
                FirebaseAuth.getInstance().signOut()
                finish()
            }
            R.id.menu_item_profile -> {
                Log.d("TODOAPPLICATION", "onOptionsItemSelected, Profile cliecked")
            }
        }
        return super.onOptionsItemSelected(item)
    }
}