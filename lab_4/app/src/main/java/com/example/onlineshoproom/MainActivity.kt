package com.example.onlineshoproom

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView


class MainActivity : Activity() {
    private lateinit var dao: ShopDao
    private lateinit var output: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dao = ShopDatabase.getDatabase(this).shopDao()

        val scrollView = ScrollView(this)
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(dp(16), dp(16), dp(16), dp(16))
        scrollView.addView(root)

        root.addView(createTitle("База даних інтернет-магазину"))
        root.addView(createText("Room ORM: таблиці, зв'язки, CRUD, індекси та оптимізовані запити."))

        val seedButton = createButton("Створити тестові дані")
        val usersButton = createButton("Користувачі із замовленнями")
        val ordersButton = createButton("Замовлення з товарами")
        val searchButton = createButton("Пошук товарів: Ноутбук")
        val categoryButton = createButton("Товари категорії Електроніка")
        val reviewsButton = createButton("Відгуки з користувачами")

        output = createText("")

        seedButton.setOnClickListener {
            seedData()
            output.text = "Тестові дані створено."
        }

        usersButton.setOnClickListener {
            showUsersWithOrders()
        }

        ordersButton.setOnClickListener {
            showOrdersWithProducts()
        }

        searchButton.setOnClickListener {
            showSearchProducts()
        }

        categoryButton.setOnClickListener {
            showProductsByCategory()
        }

        reviewsButton.setOnClickListener {
            showReviews()
        }

        root.addView(seedButton)
        root.addView(usersButton)
        root.addView(ordersButton)
        root.addView(searchButton)
        root.addView(categoryButton)
        root.addView(reviewsButton)
        root.addView(output)

        setContentView(scrollView)
    }

    private fun seedData() {
        dao.clearAllTables()

        val user1Id = dao.insertUser(
            UserEntity(name = "Іван Петренко", email = "ivan@example.com", phone = "+380501112233")
        ).toInt()
        val user2Id = dao.insertUser(
            UserEntity(name = "Олена Коваль", email = "olena@example.com", phone = "+380672224455")
        ).toInt()

        val electronicsId = dao.insertCategory(
            CategoryEntity(name = "Електроніка", description = "Смартфони, ноутбуки та аксесуари")
        ).toInt()
        val clothesId = dao.insertCategory(
            CategoryEntity(name = "Одяг", description = "Чоловічий та жіночий одяг")
        ).toInt()
        val booksId = dao.insertCategory(
            CategoryEntity(name = "Книги", description = "Паперові та електронні книги")
        ).toInt()

        val phoneId = dao.insertProduct(
            ProductEntity(
                name = "Смартфон Galaxy",
                sku = "EL-001",
                description = "Смартфон з великим екраном",
                price = 14999.0,
                stock = 12
            )
        ).toInt()
        val laptopId = dao.insertProduct(
            ProductEntity(
                name = "Ноутбук ProBook",
                sku = "EL-002",
                description = "Ноутбук для навчання і роботи",
                price = 32999.0,
                stock = 5
            )
        ).toInt()
        val tshirtId = dao.insertProduct(
            ProductEntity(
                name = "Футболка Basic",
                sku = "CL-001",
                description = "Базова футболка",
                price = 499.0,
                stock = 40
            )
        ).toInt()
        val bookId = dao.insertProduct(
            ProductEntity(
                name = "Книга Kotlin Start",
                sku = "BK-001",
                description = "Книга для початку роботи з Kotlin",
                price = 350.0,
                stock = 20
            )
        ).toInt()

        dao.insertProductCategory(ProductCategoryCrossRef(phoneId, electronicsId))
        dao.insertProductCategory(ProductCategoryCrossRef(laptopId, electronicsId))
        dao.insertProductCategory(ProductCategoryCrossRef(tshirtId, clothesId))
        dao.insertProductCategory(ProductCategoryCrossRef(bookId, booksId))

        val order1Id = dao.insertOrder(OrderEntity(userId = user1Id, status = "paid")).toInt()
        val order2Id = dao.insertOrder(OrderEntity(userId = user2Id, status = "new")).toInt()

        dao.insertOrderProduct(OrderProductCrossRef(order1Id, phoneId, 1, 14999.0))
        dao.insertOrderProduct(OrderProductCrossRef(order1Id, bookId, 1, 350.0))
        dao.insertOrderProduct(OrderProductCrossRef(order2Id, laptopId, 1, 32999.0))
        dao.insertOrderProduct(OrderProductCrossRef(order2Id, tshirtId, 2, 499.0))

        dao.insertReview(
            ReviewEntity(userId = user1Id, productId = phoneId, rating = 5, comment = "Гарний смартфон")
        )
        dao.insertReview(
            ReviewEntity(userId = user2Id, productId = laptopId, rating = 4, comment = "Ноутбук швидкий")
        )
        dao.insertReview(
            ReviewEntity(userId = user1Id, productId = bookId, rating = 5, comment = "Корисна книга")
        )
    }

    private fun showUsersWithOrders() {
        val result = StringBuilder()
        result.append("Користувачі із замовленнями:\n")

        for (item in dao.getUsersWithOrders()) {
            result.append(item.user.name)
            result.append(" - замовлень: ")
            result.append(item.orders.size)
            result.append("\n")
        }

        output.text = result.toString()
    }

    private fun showOrdersWithProducts() {
        val result = StringBuilder()
        result.append("Замовлення з користувачами і товарами:\n")

        for (item in dao.getOrdersWithUserAndProducts()) {
            result.append("Замовлення #")
            result.append(item.order.id)
            result.append(", статус: ")
            result.append(item.order.status)
            result.append(", користувач: ")
            result.append(item.user.name)
            result.append("\nТовари: ")

            for (product in item.products) {
                result.append(product.name)
                result.append("; ")
            }

            result.append("\n\n")
        }

        output.text = result.toString()
    }

    private fun showSearchProducts() {
        val result = StringBuilder()
        result.append("Пошук товарів зі словом 'Ноутбук':\n")

        for (item in dao.searchProductsWithCategories("Ноутбук")) {
            result.append(item.product.name)
            result.append(" - ")
            result.append(item.product.price)
            result.append(" грн\n")
        }

        output.text = result.toString()
    }

    private fun showProductsByCategory() {
        val result = StringBuilder()
        result.append("Товари категорії Електроніка:\n")

        for (item in dao.getProductsByCategory("Електроніка")) {
            result.append(item.product.name)
            result.append(" - ")
            result.append(item.product.price)
            result.append(" грн\n")
        }

        output.text = result.toString()
    }

    private fun showReviews() {
        val result = StringBuilder()
        result.append("Відгуки з користувачами та товарами:\n")

        for (item in dao.getReviewsWithDetails()) {
            result.append(item.user.name)
            result.append(" -> ")
            result.append(item.product.name)
            result.append(", оцінка: ")
            result.append(item.review.rating)
            result.append(", коментар: ")
            result.append(item.review.comment)
            result.append("\n")
        }

        output.text = result.toString()
    }

    private fun createTitle(text: String): TextView {
        val view = createText(text)
        view.textSize = 25f
        return view
    }

    private fun createText(text: String): TextView {
        val view = TextView(this)
        view.text = text
        view.textSize = 16f
        view.setPadding(0, dp(8), 0, dp(8))
        return view
    }

    private fun createButton(text: String): Button {
        val button = Button(this)
        button.text = text
        return button
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
