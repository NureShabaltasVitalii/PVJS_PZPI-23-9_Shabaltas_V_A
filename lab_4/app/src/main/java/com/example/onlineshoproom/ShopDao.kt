package com.example.onlineshoproom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update


@Dao
interface ShopDao {
    @Insert
    fun insertUser(user: UserEntity): Long

    @Update
    fun updateUser(user: UserEntity)

    @Delete
    fun deleteUser(user: UserEntity)

    @Insert
    fun insertProduct(product: ProductEntity): Long

    @Update
    fun updateProduct(product: ProductEntity)

    @Delete
    fun deleteProduct(product: ProductEntity)

    @Insert
    fun insertOrder(order: OrderEntity): Long

    @Update
    fun updateOrder(order: OrderEntity)

    @Delete
    fun deleteOrder(order: OrderEntity)

    @Insert
    fun insertCategory(category: CategoryEntity): Long

    @Update
    fun updateCategory(category: CategoryEntity)

    @Delete
    fun deleteCategory(category: CategoryEntity)

    @Insert
    fun insertReview(review: ReviewEntity): Long

    @Update
    fun updateReview(review: ReviewEntity)

    @Delete
    fun deleteReview(review: ReviewEntity)

    @Insert
    fun insertProductCategory(crossRef: ProductCategoryCrossRef)

    @Insert
    fun insertOrderProduct(crossRef: OrderProductCrossRef)

    @Query("SELECT * FROM users ORDER BY id")
    fun getUsers(): List<UserEntity>

    @Query("SELECT * FROM products ORDER BY name")
    fun getProducts(): List<ProductEntity>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getOrders(): List<OrderEntity>

    @Query("SELECT * FROM categories ORDER BY name")
    fun getCategories(): List<CategoryEntity>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getReviews(): List<ReviewEntity>

    @Transaction
    @Query("SELECT * FROM users ORDER BY id")
    fun getUsersWithOrders(): List<UserWithOrders>

    @Transaction
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getOrdersWithUserAndProducts(): List<OrderWithUserAndProducts>

    @Transaction
    @Query("SELECT * FROM products WHERE name LIKE '%' || :searchText || '%' ORDER BY name")
    fun searchProductsWithCategories(searchText: String): List<ProductWithCategories>

    @Transaction
    @Query(
        "SELECT products.* FROM products " +
            "INNER JOIN product_categories ON products.id = product_categories.productId " +
            "INNER JOIN categories ON categories.id = product_categories.categoryId " +
            "WHERE categories.name = :categoryName " +
            "ORDER BY products.price"
    )
    fun getProductsByCategory(categoryName: String): List<ProductWithCategories>

    @Transaction
    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getReviewsWithDetails(): List<ReviewWithDetails>

    @Query("DELETE FROM order_products")
    fun deleteOrderProducts()

    @Query("DELETE FROM product_categories")
    fun deleteProductCategories()

    @Query("DELETE FROM reviews")
    fun deleteReviews()

    @Query("DELETE FROM orders")
    fun deleteOrders()

    @Query("DELETE FROM products")
    fun deleteProducts()

    @Query("DELETE FROM categories")
    fun deleteCategories()

    @Query("DELETE FROM users")
    fun deleteUsers()

    @Transaction
    fun clearAllTables() {
        deleteOrderProducts()
        deleteProductCategories()
        deleteReviews()
        deleteOrders()
        deleteProducts()
        deleteCategories()
        deleteUsers()
    }
}
