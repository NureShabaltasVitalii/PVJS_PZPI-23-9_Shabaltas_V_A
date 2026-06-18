package com.example.onlineshoproom

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String
)


@Entity(
    tableName = "products",
    indices = [
        Index(value = ["name"]),
        Index(value = ["sku"], unique = true),
        Index(value = ["name", "price"])
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val sku: String,
    val description: String,
    val price: Double,
    val stock: Int
)


@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["status"]),
        Index(value = ["createdAt"]),
        Index(value = ["userId", "status"])
    ]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)


@Entity(
    tableName = "categories",
    indices = [Index(value = ["name"], unique = true)]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String
)


@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["productId"]),
        Index(value = ["userId", "productId"], unique = true),
        Index(value = ["productId", "rating"])
    ]
)
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val productId: Int,
    val rating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)


@Entity(
    tableName = "product_categories",
    primaryKeys = ["productId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class ProductCategoryCrossRef(
    val productId: Int,
    val categoryId: Int
)


@Entity(
    tableName = "order_products",
    primaryKeys = ["orderId", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["productId"])]
)
data class OrderProductCrossRef(
    val orderId: Int,
    val productId: Int,
    val quantity: Int,
    val price: Double
)


data class UserWithOrders(
    @Embedded val user: UserEntity,
    @Relation(parentColumn = "id", entityColumn = "userId")
    val orders: List<OrderEntity>
)


data class ProductWithCategories(
    @Embedded val product: ProductEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProductCategoryCrossRef::class,
            parentColumn = "productId",
            entityColumn = "categoryId"
        )
    )
    val categories: List<CategoryEntity>
)


data class OrderWithUserAndProducts(
    @Embedded val order: OrderEntity,
    @Relation(parentColumn = "userId", entityColumn = "id")
    val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = OrderProductCrossRef::class,
            parentColumn = "orderId",
            entityColumn = "productId"
        )
    )
    val products: List<ProductEntity>
)


data class ReviewWithDetails(
    @Embedded val review: ReviewEntity,
    @Relation(parentColumn = "userId", entityColumn = "id")
    val user: UserEntity,
    @Relation(parentColumn = "productId", entityColumn = "id")
    val product: ProductEntity
)
