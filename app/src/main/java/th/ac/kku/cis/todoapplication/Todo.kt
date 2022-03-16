package th.ac.kku.cis.todoapplication

class Todo {
    companion object Factory {
        fun create(): Todo = Todo()
    }
    var objectId: String? = null
    var todoText: String? = null
    var done: Boolean = false
}