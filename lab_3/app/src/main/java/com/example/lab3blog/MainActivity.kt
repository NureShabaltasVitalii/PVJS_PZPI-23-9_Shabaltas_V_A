package com.example.lab3blog

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : Activity() {
    private val categories = arrayOf("Усі", "Навчання", "Новини", "Особисте", "Інше")
    private val storageName = "blog_storage"
    private val usersKey = "users"
    private val postsKey = "posts"
    private val currentUserKey = "current_user"

    private lateinit var rootLayout: LinearLayout
    private lateinit var postsContainer: LinearLayout
    private lateinit var titleInput: EditText
    private lateinit var contentInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var searchInput: EditText
    private lateinit var filterSpinner: Spinner
    private lateinit var authInfo: TextView

    private var currentUser = ""
    private var editPostId = -1
    private var selectedCategory = "Навчання"
    private var selectedFilter = "Усі"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createDefaultData()
        currentUser = getStorage().getString(currentUserKey, "") ?: ""
        buildScreen()
    }

    private fun getStorage() = getSharedPreferences(storageName, MODE_PRIVATE)

    private fun buildScreen() {
        val scrollView = ScrollView(this)
        rootLayout = LinearLayout(this)
        rootLayout.orientation = LinearLayout.VERTICAL
        rootLayout.setPadding(dp(16), dp(16), dp(16), dp(16))
        scrollView.addView(rootLayout)
        setContentView(scrollView)

        val title = TextView(this)
        title.text = "Простий блог"
        title.textSize = 26f
        title.setPadding(0, 0, 0, dp(12))
        rootLayout.addView(title)

        if (currentUser == "") {
            addAuthBlock()
        } else {
            addBlogBlock()
        }
    }

    private fun addAuthBlock() {
        val loginInput = createInput("Логін")
        val passwordInput = createInput("Пароль")

        val loginButton = createButton("Увійти")
        loginButton.setOnClickListener {
            val login = loginInput.text.toString()
            val password = passwordInput.text.toString()

            if (checkUser(login, password)) {
                currentUser = login
                getStorage().edit().putString(currentUserKey, currentUser).apply()
                reloadScreen()
            } else {
                showMessage("Неправильний логін або пароль")
            }
        }

        val registerButton = createButton("Зареєструватися")
        registerButton.setOnClickListener {
            val login = loginInput.text.toString()
            val password = passwordInput.text.toString()

            if (login == "" || password == "") {
                showMessage("Введіть логін і пароль")
            } else if (userExists(login)) {
                showMessage("Такий користувач вже існує")
            } else {
                addUser(login, password)
                currentUser = login
                getStorage().edit().putString(currentUserKey, currentUser).apply()
                reloadScreen()
            }
        }

        rootLayout.addView(createSectionTitle("Авторизація"))
        rootLayout.addView(loginInput)
        rootLayout.addView(passwordInput)
        rootLayout.addView(loginButton)
        rootLayout.addView(registerButton)
    }

    private fun addBlogBlock() {
        authInfo = createText("Користувач: $currentUser")

        val logoutButton = createButton("Вийти")
        logoutButton.setOnClickListener {
            currentUser = ""
            getStorage().edit().putString(currentUserKey, "").apply()
            reloadScreen()
        }

        rootLayout.addView(authInfo)
        rootLayout.addView(logoutButton)
        rootLayout.addView(createSectionTitle("Нова стаття"))

        titleInput = createInput("Заголовок статті")
        contentInput = createInput("Текст статті")
        contentInput.minLines = 4

        categorySpinner = createSpinner(categories.drop(1).toTypedArray())
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position + 1]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val saveButton = createButton("Зберегти статтю")
        saveButton.setOnClickListener {
            savePost()
        }

        val cancelEditButton = createButton("Скасувати редагування")
        cancelEditButton.setOnClickListener {
            clearPostForm()
        }

        rootLayout.addView(titleInput)
        rootLayout.addView(contentInput)
        rootLayout.addView(categorySpinner)
        rootLayout.addView(saveButton)
        rootLayout.addView(cancelEditButton)

        rootLayout.addView(createSectionTitle("Пошук і фільтр"))
        searchInput = createInput("Пошук за заголовком або текстом")
        filterSpinner = createSpinner(categories)
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedFilter = categories[position]
                renderPosts()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val searchButton = createButton("Знайти")
        searchButton.setOnClickListener {
            renderPosts()
        }

        rootLayout.addView(searchInput)
        rootLayout.addView(filterSpinner)
        rootLayout.addView(searchButton)

        rootLayout.addView(createSectionTitle("Статті"))
        postsContainer = LinearLayout(this)
        postsContainer.orientation = LinearLayout.VERTICAL
        rootLayout.addView(postsContainer)

        renderPosts()
    }

    private fun reloadScreen() {
        rootLayout.removeAllViews()
        buildScreen()
    }

    private fun createDefaultData() {
        val storage = getStorage()

        if (!storage.contains(usersKey)) {
            val users = JSONArray()
            val user = JSONObject()
            user.put("login", "admin")
            user.put("password", "1234")
            users.put(user)
            storage.edit().putString(usersKey, users.toString()).apply()
        }

        if (!storage.contains(postsKey)) {
            val posts = JSONArray()
            posts.put(createPostObject(1, "Перша стаття", "Це приклад статті у простому блозі.", "Навчання", "admin"))
            posts.put(createPostObject(2, "Новини про проєкт", "У блозі можна додавати, редагувати та видаляти записи.", "Новини", "admin"))
            storage.edit().putString(postsKey, posts.toString()).apply()
        }
    }

    private fun checkUser(login: String, password: String): Boolean {
        val users = getUsers()

        for (i in 0 until users.length()) {
            val user = users.getJSONObject(i)

            if (user.getString("login") == login && user.getString("password") == password) {
                return true
            }
        }

        return false
    }

    private fun userExists(login: String): Boolean {
        val users = getUsers()

        for (i in 0 until users.length()) {
            if (users.getJSONObject(i).getString("login") == login) {
                return true
            }
        }

        return false
    }

    private fun addUser(login: String, password: String) {
        val users = getUsers()
        val user = JSONObject()
        user.put("login", login)
        user.put("password", password)
        users.put(user)
        getStorage().edit().putString(usersKey, users.toString()).apply()
    }

    private fun savePost() {
        val title = titleInput.text.toString()
        val content = contentInput.text.toString()

        if (title == "" || content == "") {
            showMessage("Заповніть заголовок і текст статті")
            return
        }

        val posts = getPosts()

        if (editPostId == -1) {
            val newId = getNextPostId(posts)
            posts.put(createPostObject(newId, title, content, selectedCategory, currentUser))
        } else {
            for (i in 0 until posts.length()) {
                val post = posts.getJSONObject(i)

                if (post.getInt("id") == editPostId) {
                    post.put("title", title)
                    post.put("content", content)
                    post.put("category", selectedCategory)
                }
            }
        }

        savePosts(posts)
        clearPostForm()
        renderPosts()
        showMessage("Статтю збережено")
    }

    private fun createPostObject(id: Int, title: String, content: String, category: String, author: String): JSONObject {
        val post = JSONObject()
        post.put("id", id)
        post.put("title", title)
        post.put("content", content)
        post.put("category", category)
        post.put("author", author)
        post.put("comments", JSONArray())
        return post
    }

    private fun renderPosts() {
        postsContainer.removeAllViews()

        val posts = getPosts()
        val searchText = searchInput.text.toString().lowercase()
        var visibleCount = 0

        for (i in 0 until posts.length()) {
            val post = posts.getJSONObject(i)
            val title = post.getString("title")
            val content = post.getString("content")
            val category = post.getString("category")

            val matchesSearch = title.lowercase().contains(searchText) || content.lowercase().contains(searchText)
            val matchesCategory = selectedFilter == "Усі" || category == selectedFilter

            if (matchesSearch && matchesCategory) {
                postsContainer.addView(createPostView(post))
                visibleCount++
            }
        }

        if (visibleCount == 0) {
            postsContainer.addView(createText("Статей не знайдено."))
        }
    }

    private fun createPostView(post: JSONObject): LinearLayout {
        val block = LinearLayout(this)
        block.orientation = LinearLayout.VERTICAL
        block.setPadding(dp(12), dp(12), dp(12), dp(12))

        val title = createText(post.getString("title"))
        title.textSize = 20f

        val category = createText("Категорія: " + post.getString("category"))
        val author = createText("Автор: " + post.getString("author"))
        val content = createText(post.getString("content"))

        val editButton = createButton("Редагувати")
        editButton.setOnClickListener {
            editPostId = post.getInt("id")
            titleInput.setText(post.getString("title"))
            contentInput.setText(post.getString("content"))
            setCategorySpinnerValue(post.getString("category"))
            showMessage("Редагування статті")
        }

        val deleteButton = createButton("Видалити")
        deleteButton.setOnClickListener {
            deletePost(post.getInt("id"))
        }

        val commentTitle = createText("Коментарі:")
        val comments = post.getJSONArray("comments")
        val commentsBlock = LinearLayout(this)
        commentsBlock.orientation = LinearLayout.VERTICAL

        if (comments.length() == 0) {
            commentsBlock.addView(createText("Коментарів ще немає."))
        }

        for (i in 0 until comments.length()) {
            val comment = comments.getJSONObject(i)
            commentsBlock.addView(createText(comment.getString("author") + ": " + comment.getString("text")))
        }

        val commentInput = createInput("Новий коментар")
        val addCommentButton = createButton("Додати коментар")
        addCommentButton.setOnClickListener {
            addComment(post.getInt("id"), commentInput.text.toString())
        }

        block.addView(title)
        block.addView(category)
        block.addView(author)
        block.addView(content)
        block.addView(editButton)
        block.addView(deleteButton)
        block.addView(commentTitle)
        block.addView(commentsBlock)
        block.addView(commentInput)
        block.addView(addCommentButton)

        return block
    }

    private fun deletePost(id: Int) {
        val oldPosts = getPosts()
        val newPosts = JSONArray()

        for (i in 0 until oldPosts.length()) {
            val post = oldPosts.getJSONObject(i)

            if (post.getInt("id") != id) {
                newPosts.put(post)
            }
        }

        savePosts(newPosts)
        clearPostForm()
        renderPosts()
        showMessage("Статтю видалено")
    }

    private fun addComment(postId: Int, text: String) {
        if (text == "") {
            showMessage("Введіть текст коментаря")
            return
        }

        val posts = getPosts()

        for (i in 0 until posts.length()) {
            val post = posts.getJSONObject(i)

            if (post.getInt("id") == postId) {
                val comments = post.getJSONArray("comments")
                val comment = JSONObject()
                comment.put("author", currentUser)
                comment.put("text", text)
                comments.put(comment)
            }
        }

        savePosts(posts)
        renderPosts()
        showMessage("Коментар додано")
    }

    private fun clearPostForm() {
        editPostId = -1
        titleInput.setText("")
        contentInput.setText("")
        categorySpinner.setSelection(0)
    }

    private fun setCategorySpinnerValue(category: String) {
        for (i in 1 until categories.size) {
            if (categories[i] == category) {
                categorySpinner.setSelection(i - 1)
            }
        }
    }

    private fun getNextPostId(posts: JSONArray): Int {
        var maxId = 0

        for (i in 0 until posts.length()) {
            val id = posts.getJSONObject(i).getInt("id")

            if (id > maxId) {
                maxId = id
            }
        }

        return maxId + 1
    }

    private fun getUsers(): JSONArray {
        val text = getStorage().getString(usersKey, "[]") ?: "[]"
        return JSONArray(text)
    }

    private fun getPosts(): JSONArray {
        val text = getStorage().getString(postsKey, "[]") ?: "[]"
        return JSONArray(text)
    }

    private fun savePosts(posts: JSONArray) {
        getStorage().edit().putString(postsKey, posts.toString()).apply()
    }

    private fun createInput(hintText: String): EditText {
        val input = EditText(this)
        input.hint = hintText
        input.setSingleLine(false)
        input.setPadding(dp(8), dp(8), dp(8), dp(8))
        return input
    }

    private fun createButton(text: String): Button {
        val button = Button(this)
        button.text = text
        return button
    }

    private fun createText(text: String): TextView {
        val view = TextView(this)
        view.text = text
        view.textSize = 16f
        view.setPadding(0, dp(6), 0, dp(6))
        return view
    }

    private fun createSectionTitle(text: String): TextView {
        val view = createText(text)
        view.textSize = 22f
        view.setPadding(0, dp(18), 0, dp(8))
        return view
    }

    private fun createSpinner(items: Array<String>): Spinner {
        val spinner = Spinner(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        return spinner
    }

    private fun showMessage(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
