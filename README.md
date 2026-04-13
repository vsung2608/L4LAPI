# L4L — Listening for learning

Nền tảng học ngoại ngữ trực tuyến xây dựng theo kiến trúc **Microservices** với Spring Boot & Spring Cloud.

---

## Kiến trúc tổng quan

```
Client
  │
  ▼
API Gateway  ──► Config Server
  │               Discovery Server (Eureka)
  ├──► Auth Service
  ├──► Learning Service
  ├──► Progress Service
  ├──► Content Service
  ├──► Community Service
  ├──► Payment Service
  ├──► Notification Service
  └──► Analytics Service
```

Tất cả các service đăng ký với **Eureka Discovery Server**, lấy cấu hình từ **Config Server**, và được truy cập duy nhất qua **API Gateway**.

---

## Các module

| Module | Mô tả |
|---|---|
| `common` | Thư viện dùng chung (DTO, exception, response wrapper) |
| `api_gateway` | Cổng vào duy nhất — JWT validation, routing, Resilience4j |
| `config_server` | Quản lý cấu hình tập trung (Spring Cloud Config) |
| `discovery_server` | Service registry (Netflix Eureka) |
| `auth_service` | Xác thực, phân quyền, quản lý người dùng |
| `learning_service` | Bài học, danh mục, thẻ từ vựng (Flashcard Deck/Card) |
| `progress_service` | Theo dõi tiến độ học tập của người dùng |
| `content_service` | Quản lý bài viết / blog |
| `community_service` | Bình luận, đánh giá, nhóm chat |
| `payment_service` | Thanh toán VNPay, quản lý gói VIP |
| `notification_service` | Gửi email thông báo qua Kafka |
| `analytics_service` | Phân tích dữ liệu học tập |

---

## Tech Stack

- **Java 23** · **Spring Boot 4.0.2** · **Spring Cloud 2025.1.0**
- **Spring Cloud Gateway** (WebFlux) — API Gateway
- **Netflix Eureka** — Service Discovery
- **Spring Cloud Config** — Centralized Configuration
- **Spring Security** + **JWT** (jjwt 0.12.3)
- **Spring Data JPA** + **PostgreSQL 16**
- **Redis** — Token blacklist / cache
- **Redpanda (Kafka-compatible)** — Async messaging
- **OpenFeign** — Inter-service HTTP calls
- **Resilience4j** — Circuit breaker
- **Cloudinary** — File/image storage (learning_service)
- **Microsoft Azure Cognitive Services Speech SDK** — TTS (learning_service)
- **VNPay** — Payment gateway
- **Lombok** · **SpringDoc OpenAPI (Swagger UI)**

---

## API chính

### Auth Service — `/api/v1/auth`

| Method | Path | Mô tả |
|---|---|---|
| POST | `/register` | Đăng ký tài khoản |
| POST | `/login` | Đăng nhập, trả về access token + refresh token cookie |
| POST | `/oauth2/google` | Đăng nhập bằng Google OAuth2 |
| GET | `/verify-email` | Xác thực email |
| POST | `/refresh` | Làm mới access token |
| POST | `/logout` | Đăng xuất |
| POST | `/forgot-password` | Yêu cầu đặt lại mật khẩu |
| POST | `/reset-password` | Đặt lại mật khẩu mới |

### User Service — `/api/v1/users`

| Method | Path | Mô tả |
|---|---|---|
| GET | `/summary` | Lấy thông tin tóm tắt người dùng |
| GET | `/detail` | Lấy chi tiết profile |
| PUT | `/` | Cập nhật profile |
| PUT | `/settings` | Cập nhật cài đặt |
| GET | `/vip` | Xem trạng thái VIP |
| POST | `/vip/cancel` | Hủy VIP |

### Learning Service — `/api/v1/lessons`, `/api/v1/decks`, `/api/v1/cards`

| Method | Path | Mô tả |
|---|---|---|
| GET | `/lessons/catalog` | Danh mục bài học theo ngôn ngữ |
| GET | `/lessons/{id}/detail` | Chi tiết bài học kèm câu ví dụ |
| GET | `/lessons/level/{level}` | Bài học theo cấp JLPT |
| GET | `/decks` | Danh sách bộ thẻ từ vựng |
| CRUD | `/decks`, `/cards` | Quản lý deck & card (Admin) |

### Progress Service — `/api/v1/progress`

| Method | Path | Mô tả |
|---|---|---|
| POST | `/lessons/start` | Bắt đầu học bài |
| PATCH | `/lessons` | Cập nhật tiến độ |
| GET | `/lessons` | Lấy tiến độ bài học |
| GET | `/users/{userId}` | Toàn bộ tiến độ của user |
| POST | `/decks/{deckId}/cards/{cardId}/record` | Ghi kết quả ôn thẻ |
| GET | `/analyst` | Thống kê học tập hàng ngày |

### Content Service — `/api/v1/articles`

| Method | Path | Mô tả |
|---|---|---|
| GET | `/` | Danh sách bài viết đã publish |
| GET | `/slug/{slug}` | Xem bài viết theo slug (tăng viewCount) |
| GET | `/featured` | Bài viết nổi bật |
| PATCH | `/{id}/publish` | Publish bài viết |
| CRUD | `/articles` | Quản lý bài viết (Admin) |

### Community Service — `/api/v1/ratings`, `/api/v1/comments`

| Method | Path | Mô tả |
|---|---|---|
| POST | `/ratings` | Đánh giá nội dung |
| GET | `/ratings/stats` | Thống kê điểm đánh giá |
| CRUD | `/comments` | Bình luận |
| CRUD | `/chat-groups`, `/group-messages` | Nhóm chat cộng đồng |

### Payment Service — `/api/v1/payment`

| Method | Path | Mô tả |
|---|---|---|
| POST | `/create` | Tạo giao dịch thanh toán VNPay |
| GET | `/vnpay-return` | Callback từ VNPay |
| GET | `/history` | Lịch sử thanh toán của user |
| GET | `/{paymentId}` | Chi tiết giao dịch |

---

## Yêu cầu hệ thống

- **JDK 23+**
- **Maven 3.9+**
- **Docker & Docker Compose**

---

## Khởi động nhanh

### 1. Khởi động infrastructure

```bash
docker-compose up -d
```

Sẽ khởi động:
- **PostgreSQL 16** — port `5432`
- **Redpanda (Kafka)** — port `9092`

### 2. Build toàn bộ project

```bash
./mvnw clean install -DskipTests
```

### 3. Khởi động các service theo thứ tự

```bash
# 1. Config Server (phải chạy trước)
cd config_server && ./mvnw spring-boot:run

# 2. Discovery Server
cd discovery_server && ./mvnw spring-boot:run

# 3. Các business service (có thể chạy song song)
cd auth_service      && ./mvnw spring-boot:run
cd learning_service  && ./mvnw spring-boot:run
cd progress_service  && ./mvnw spring-boot:run
cd content_service   && ./mvnw spring-boot:run
cd community_service && ./mvnw spring-boot:run
cd payment_service   && ./mvnw spring-boot:run
cd notification_service && ./mvnw spring-boot:run

# 4. API Gateway (chạy sau cùng)
cd api_gateway && ./mvnw spring-boot:run
```

---

## Bảo mật

- **JWT Access Token** — lưu ở client (Authorization header)
- **Refresh Token** — lưu trong HttpOnly Secure Cookie
- **Token blacklist** — lưu trên Redis khi logout
- **API Gateway** phân quyền:
  - `PUBLIC` — các GET endpoint không cần đăng nhập
  - `USER` — cần JWT hợp lệ
  - `ADMIN` — POST/PUT/DELETE các resource quản trị
- Header `X-User-Id` được inject bởi Gateway sau khi xác thực JWT

---

## Cấu trúc thư mục

```
LJL/
├── common/                  # Shared library
├── api_gateway/             # API Gateway
├── config_server/           # Config Server
├── discovery_server/        # Eureka Server
├── auth_service/            # Authentication & User
├── learning_service/        # Lessons, Decks, Cards
├── progress_service/        # Learning Progress
├── content_service/         # Articles / Blog
├── community_service/       # Comments, Ratings, Chat
├── payment_service/         # VNPay Integration
├── notification_service/    # Email Notifications
├── analytics_service/       # Analytics
└── docker-compose.yml       # Infrastructure
```

---

## Liên lạc

- **Group ID:** `com.v1no`
- **Version:** `0.0.1`
