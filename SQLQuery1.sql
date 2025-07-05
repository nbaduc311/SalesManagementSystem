USE [BLK]; -- Đảm bảo bạn đang sử dụng đúng tên database của mình

-- Bắt đầu xóa dữ liệu và bảng (theo thứ tự khóa ngoại ngược)
PRINT N'Đang xóa các bảng hiện có...';
GO

IF OBJECT_ID('ChiTietHoaDon', 'U') IS NOT NULL DROP TABLE ChiTietHoaDon;
IF OBJECT_ID('HoaDon', 'U') IS NOT NULL DROP TABLE HoaDon;
IF OBJECT_ID('ChiTietPhieuNhap', 'U') IS NOT NULL DROP TABLE ChiTietPhieuNhap;
IF OBJECT_ID('PhieuNhapHang', 'U') IS NOT NULL DROP TABLE PhieuNhapHang;
IF OBJECT_ID('ChiTietViTri', 'U') IS NOT NULL DROP TABLE ChiTietViTri;
IF OBJECT_ID('PhucHoi', 'U') IS NOT NULL DROP TABLE PhucHoi;
IF OBJECT_ID('SaoLuu', 'U') IS NOT NULL DROP TABLE SaoLuu;
IF OBJECT_ID('SanPham', 'U') IS NOT NULL DROP TABLE SanPham;
IF OBJECT_ID('ViTriDungSanPham', 'U') IS NOT NULL DROP TABLE ViTriDungSanPham;
IF OBJECT_ID('LoaiSanPham', 'U') IS NOT NULL DROP TABLE LoaiSanPham;
IF OBJECT_ID('NhanVien', 'U') IS NOT NULL DROP TABLE NhanVien;
IF OBJECT_ID('KhachHang', 'U') IS NOT NULL DROP TABLE KhachHang;
IF OBJECT_ID('TaiKhoanNguoiDung', 'U') IS NOT NULL DROP TABLE TaiKhoanNguoiDung;
GO

PRINT N'Đã xóa xong các bảng.';
PRINT N'Đang tạo lại các bảng...';
GO

-- Tạo lại các bảng theo định nghĩa của bạn
CREATE TABLE TaiKhoanNguoiDung (
    InternalID INT IDENTITY(1,1) NOT NULL,
    MaNguoiDung AS ('TK' + RIGHT('000' + CAST(InternalID AS VARCHAR(10)), 3)) PERSISTED PRIMARY KEY,
    Username NVARCHAR(50) NOT NULL UNIQUE ,
    Password NVARCHAR(255) NOT NULL,
    Email NVARCHAR(100) NOT NULL UNIQUE,
    LoaiNguoiDung NVARCHAR(20) NOT NULL,
    NgayTao DATETIME NOT NULL CONSTRAINT DF_TaiKhoan_NgayTao DEFAULT CURRENT_TIMESTAMP,
    TrangThaiTaiKhoan NVARCHAR(20) NOT NULL CONSTRAINT DF_TaiKhoan_TrangThai DEFAULT N'Hoạt động',

    CONSTRAINT CK_TaiKhoan_LoaiNguoiDung
        CHECK (LoaiNguoiDung IN (N'Admin', N'Nhân viên', N'Khách hàng')),
    CONSTRAINT CK_TaiKhoan_TrangThai
        CHECK (TrangThaiTaiKhoan IN (N'Hoạt động', N'Bị khóa', N'Vô hiệu hóa'))
);

CREATE TABLE KhachHang (
    InternalID INT IDENTITY(1,1) NOT NULL,
    MaKhachHang AS ('KH' + RIGHT('000' + CAST(InternalID AS VARCHAR(10)), 3)) PERSISTED PRIMARY KEY,
    MaNguoiDung VARCHAR(5) NULL,
    HoTen NVARCHAR(100) NOT NULL,
    NgaySinh DATE NULL,
    GioiTinh NVARCHAR(10) NULL,
    SDT NVARCHAR(15) NOT NULL UNIQUE,

    CONSTRAINT FK_KhachHang_TaiKhoan FOREIGN KEY (MaNguoiDung)
        REFERENCES TaiKhoanNguoiDung(MaNguoiDung),

    CONSTRAINT CK_KhachHang_GioiTinh CHECK (GioiTinh IN (N'Nam', N'Nữ', N'Khác')),
    CONSTRAINT CK_KhachHang_NgaySinh CHECK (NgaySinh <= GETDATE()),
    CONSTRAINT CK_KhachHang_SDT CHECK (LEN(SDT) >= 9 AND SDT NOT LIKE '%[^0-9]%'),
);
CREATE UNIQUE INDEX UQ_KhachHang_MaNguoiDung
ON KhachHang(MaNguoiDung)
WHERE MaNguoiDung IS NOT NULL;

CREATE TABLE NhanVien (
    InternalID INT IDENTITY(1,1) NOT NULL,
    MaNhanVien AS ('NV' + RIGHT('000' + CAST(InternalID AS VARCHAR(10)), 3)) PERSISTED PRIMARY KEY,
    MaNguoiDung VARCHAR(5) NOT NULL,
    HoTen NVARCHAR(100) NOT NULL,
    NgaySinh DATE NOT NULL,
    GioiTinh NVARCHAR(10) NOT NULL,
    CCCD NVARCHAR(20) NOT NULL UNIQUE,
    SDT NVARCHAR(15) NOT NULL UNIQUE,
    Luong DECIMAL(18, 0) NOT NULL,
    TrangThaiLamViec NVARCHAR(20) NOT NULL CONSTRAINT DF_TaiKhoan_LamViec DEFAULT N'Hoạt động',

    CONSTRAINT FK_NhanVien_TaiKhoan FOREIGN KEY (MaNguoiDung)
        REFERENCES TaiKhoanNguoiDung(MaNguoiDung),
    CONSTRAINT CK_NhanVien_GioiTinh CHECK (GioiTinh IN (N'Nam', N'Nữ', N'Khác')),
    CONSTRAINT CK_NhanVien_NgaySinh CHECK (NgaySinh <= GETDATE()),
    CONSTRAINT CK_NhanVien_SDT CHECK (LEN(SDT) >= 9 AND SDT NOT LIKE '%[^0-9]%'),

    CONSTRAINT CK_TaiKhoan_LamViec
        CHECK (TrangThaiLamViec IN (N'Hoạt động', N'Nghỉ phép', N'Đã nghỉ việc')),
    CONSTRAINT UQ_NhanVien_MaNguoiDung UNIQUE (MaNguoiDung)
);


CREATE TABLE LoaiSanPham (
    InternalID INT IDENTITY(1,1) NOT NULL,
    MaLoaiSanPham AS ('LSP' + RIGHT('0000' + CAST(InternalID AS VARCHAR(10)), 4)) PERSISTED PRIMARY KEY,
    TenLoaiSanPham NVARCHAR(100) NOT NULL,
    MoTa NVARCHAR(MAX) NULL
);

CREATE TABLE SanPham (
    InternalID INT IDENTITY(1,1) NOT NULL,
    MaSanPham AS ('SP' + RIGHT('0000' + CAST(InternalID AS VARCHAR(10)), 4)) PERSISTED PRIMARY KEY,
    TenSanPham NVARCHAR(100) NOT NULL,
    DonGia DECIMAL(18, 0) NOT NULL,
    NgaySanXuat DATE NULL,
    ThongSoKyThuat NVARCHAR(MAX) NULL,
    MaLoaiSanPham VARCHAR(7) NOT NULL,
    FOREIGN KEY (MaLoaiSanPham) REFERENCES LoaiSanPham(MaLoaiSanPham)
);

CREATE TABLE ViTriDungSanPham (
    InternalID INT IDENTITY(1,1) NOT NULL,
    MaNganDung AS ('N' + RIGHT('0000' + CAST(InternalID AS VARCHAR(10)), 4)) PERSISTED PRIMARY KEY,
    TenNganDung NVARCHAR(100) NOT NULL
);

CREATE TABLE ChiTietViTri (
    MaChiTietViTri INT PRIMARY KEY IDENTITY(1,1),
    MaNganDung VARCHAR(5) NOT NULL,
    MaSanPham VARCHAR(6) NOT NULL,
    SoLuong INT NOT NULL DEFAULT 0,
    FOREIGN KEY (MaNganDung) REFERENCES ViTriDungSanPham(MaNganDung),
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham),
    CONSTRAINT UC_ChiTietViTri UNIQUE (MaNganDung, MaSanPham)
);

CREATE TABLE HoaDon (
    MaHoaDon INT PRIMARY KEY IDENTITY(1,1),
    MaKhachHang VARCHAR(5) NULL,
    TenKhachHangVangLai NVARCHAR(100) NULL,
    SDTKhachHangVangLai NVARCHAR(15) NULL,
    MaNhanVienLap VARCHAR(5) NOT NULL,
    NgayBan DATETIME NOT NULL CONSTRAINT DF_HoaDon_NgayBan DEFAULT CURRENT_TIMESTAMP,
    PhuongThucThanhToan NVARCHAR(50) NOT NULL,
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (MaKhachHang)
        REFERENCES KhachHang(MaKhachHang),
    CONSTRAINT FK_HoaDon_NhanVienLap FOREIGN KEY (MaNhanVienLap)
        REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT CK_HoaDon_PhuongThucThanhToan
        CHECK (PhuongThucThanhToan IN (N'Tiền mặt', N'Chuyển khoản', N'Thẻ tín dụng', N'Khác')),
    CONSTRAINT CK_HoaDon_KhachHangInfo CHECK (
        (MaKhachHang IS NOT NULL AND TenKhachHangVangLai IS NULL AND SDTKhachHangVangLai IS NULL) OR
        (MaKhachHang IS NULL AND TenKhachHangVangLai IS NOT NULL AND SDTKhachHangVangLai IS NOT NULL)
        )
);

-- Bảng ChiTietHoaDon
CREATE TABLE ChiTietHoaDon (
    MaChiTietHoaDon INT PRIMARY KEY IDENTITY(1,1),
    MaHoaDon INT NOT NULL,
    MaSanPham VARCHAR(6) NOT NULL,
    SoLuong INT NOT NULL CONSTRAINT CK_ChiTietHoaDon_SoLuong CHECK (SoLuong > 0),
    DonGiaBan DECIMAL(18, 0) NOT NULL CONSTRAINT CK_ChiTietHoaDon_DonGiaBan CHECK (DonGiaBan >= 0),
    ThanhTien AS (SoLuong * DonGiaBan) PERSISTED,
    CONSTRAINT FK_ChiTietHoaDon_HoaDon FOREIGN KEY (MaHoaDon)
        REFERENCES HoaDon(MaHoaDon),
    CONSTRAINT FK_ChiTietHoaDon_SanPham FOREIGN KEY (MaSanPham)
        REFERENCES SanPham(MaSanPham),
    CONSTRAINT UQ_ChiTietHoaDon_HoaDon_SanPham UNIQUE (MaHoaDon, MaSanPham)
);

-- Bảng NhaCungCap
CREATE TABLE NhaCungCap (
    InternalID INT IDENTITY(1,1) NOT NULL,
    MaNhaCungCap AS ('NCC' + RIGHT('000' + CAST(InternalID AS VARCHAR(10)), 3)) PERSISTED PRIMARY KEY,
    TenNhaCungCap NVARCHAR(255) NOT NULL,
    DiaChi NVARCHAR(255) NULL,
    SDT NVARCHAR(15) UNIQUE NULL,
    Email NVARCHAR(100) UNIQUE NULL,
    Website NVARCHAR(255) NULL,
    MoTa NVARCHAR(MAX) NULL,

    CONSTRAINT CK_NhaCungCap_SDT CHECK (SDT IS NULL OR (LEN(SDT) >= 9 AND SDT NOT LIKE '%[^0-9]%')),
    CONSTRAINT CK_NhaCungCap_Email CHECK (Email IS NULL OR Email LIKE '%_@__%.__%')
);

-- Bảng PhieuNhapHang
CREATE TABLE PhieuNhapHang (
    MaPhieuNhap INT PRIMARY KEY IDENTITY(1,1),
    NgayNhap DATETIME NOT NULL CONSTRAINT DF_PhieuNHap_NgayNhap DEFAULT CURRENT_TIMESTAMP,
    MaNhanVienThucHien VARCHAR(5) NOT NULL,
    MaNhaCungCap VARCHAR(6) NULL,

    CONSTRAINT FK_PhieuNhapHang_NhanVien FOREIGN KEY (MaNhanVienThucHien)
        REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_PhieuNhapHang_NhaCungCap FOREIGN KEY (MaNhaCungCap)
        REFERENCES NhaCungCap(MaNhaCungCap)
);

CREATE TABLE ChiTietPhieuNhap (
    MaChiTietPhieuNhap INT PRIMARY KEY IDENTITY(1,1),
    MaPhieuNhap INT NOT NULL,
    MaSanPham VARCHAR(6) NOT NULL,
    SoLuong INT NOT NULL CONSTRAINT CK_ChiTietPhieuNhap_SoLuong CHECK (SoLuong > 0),
    DonGiaNhap DECIMAL(18, 0) NOT NULL CONSTRAINT CK_ChiTietPhieuNhap_DonGiaNhap CHECK (DonGiaNhap >= 0),
    CONSTRAINT FK_ChiTietPhieuNhap_PhieuNhap FOREIGN KEY (MaPhieuNhap)
        REFERENCES PhieuNhapHang(MaPhieuNhap),
    CONSTRAINT FK_ChiTietPhieuNhap_SanPham FOREIGN KEY (MaSanPham)
        REFERENCES SanPham(MaSanPham),
    CONSTRAINT UQ_ChiTietPhieuNhap_PhieuNhap_SanPham UNIQUE (MaPhieuNhap, MaSanPham)
);

CREATE TABLE SaoLuu (
    MaSaoLuu INT PRIMARY KEY IDENTITY(1,1),
    NgaySaoLuu DATETIME NOT NULL CONSTRAINT DF_SaoLuu_NgaySaoLuu DEFAULT CURRENT_TIMESTAMP,
    LoaiSaoLuu NVARCHAR(50) NOT NULL CONSTRAINT CK_SaoLuu_LoaiSaoLuu CHECK (LoaiSaoLuu IN (N'Full', N'Differential', N'Log', N'Incremental', N'Khác')),
    MaNguoiDungThucHien VARCHAR(5) NOT NULL,
    ViTriLuuTru NVARCHAR(255) NOT NULL,

    CONSTRAINT FK_SaoLuu_TaiKhoanNguoiDung FOREIGN KEY (MaNguoiDungThucHien)
        REFERENCES TaiKhoanNguoiDung(MaNguoiDung)
);

CREATE TABLE PhucHoi (
    MaPhucHoi INT PRIMARY KEY IDENTITY(1,1),
    NgayPhucHoi DATETIME NOT NULL CONSTRAINT DF_PhucHoi_NgayPhucHoi DEFAULT CURRENT_TIMESTAMP,
    LoaiPhucHoi NVARCHAR(50) NOT NULL CONSTRAINT CK_PhucHoi_LoaiPhucHoi CHECK (LoaiPhucHoi IN (N'Full', N'Differential', N'Log', N'Khác')),
    MaNguoiDungThucHien VARCHAR(5) NOT NULL,
    MaSaoLuu INT NOT NULL,

    CONSTRAINT FK_PhucHoi_TaiKhoanNguoiDung FOREIGN KEY (MaNguoiDungThucHien)
        REFERENCES TaiKhoanNguoiDung(MaNguoiDung),
    CONSTRAINT FK_PhucHoi_SaoLuu FOREIGN KEY (MaSaoLuu)
        REFERENCES SaoLuu(MaSaoLuu)
);

PRINT N'Đã tạo lại các bảng.';
PRINT N'Đang chèn dữ liệu mẫu...';
GO

-- Chèn dữ liệu mẫu
-- TaiKhoanNguoiDung: 1 Admin, 3 Nhân viên, 2 Khách hàng
INSERT INTO TaiKhoanNguoiDung (Username, Password, Email, LoaiNguoiDung) VALUES
('admin_it', 'hashed_admin_pass', 'admin.it@compstore.com', N'Admin'),
('nv_thungan', 'hashed_nv1_pass', 'thungan@compstore.com', N'Nhân viên'),
('nv_kho', 'hashed_nv2_pass', 'kho@compstore.com', N'Nhân viên'),
('nv_kythuat', 'hashed_nv3_pass', 'kythuat@compstore.com', N'Nhân viên'),
('kh_platinum', 'hashed_kh1_pass', 'platinum@example.com', N'Khách hàng'),
('kh_silver', 'hashed_kh2_pass', 'silver@example.com', N'Khách hàng');
GO

-- NhanVien: Liên kết với các tài khoản nhân viên (bao gồm cả admin nếu admin cũng là nhân viên)
INSERT INTO NhanVien (MaNguoiDung, HoTen, NgaySinh, GioiTinh, CCCD, SDT, Luong, TrangThaiLamViec) VALUES
((SELECT MaNguoiDung FROM TaiKhoanNguoiDung WHERE Username = 'admin_it'), N'Nguyễn Đức Anh (Admin)', '1985-01-01', N'Nam', '001000000001', '0900111222', 18000000, N'Hoạt động'),
((SELECT MaNguoiDung FROM TaiKhoanNguoiDung WHERE Username = 'nv_thungan'), N'Lê Thị Hương (Thu Ngân)', '1992-03-10', N'Nữ', '002000000002', '0900222333', 9000000, N'Hoạt động'),
((SELECT MaNguoiDung FROM TaiKhoanNguoiDung WHERE Username = 'nv_kho'), N'Phạm Văn Duy (Quản lý kho)', '1990-07-25', N'Nam', '003000000003', '0900333444', 11000000, N'Hoạt động'),
((SELECT MaNguoiDung FROM TaiKhoanNguoiDung WHERE Username = 'nv_kythuat'), N'Trần Minh Khôi (Kỹ thuật)', '1993-11-12', N'Nam', '004000000004', '0900444555', 10500000, N'Hoạt động');
GO

-- KhachHang: Liên kết với các tài khoản khách hàng
INSERT INTO KhachHang (MaNguoiDung, HoTen, NgaySinh, GioiTinh, SDT) VALUES
((SELECT MaNguoiDung FROM TaiKhoanNguoiDung WHERE Username = 'kh_platinum'), N'Đặng Thị Thuỷ', '1998-09-05', N'Nữ', '0987123456'),
((SELECT MaNguoiDung FROM TaiKhoanNguoiDung WHERE Username = 'kh_silver'), N'Hoàng Anh Dũng', '2000-02-14', N'Nam', '0912987654');
GO

-- LoaiSanPham
INSERT INTO LoaiSanPham (TenLoaiSanPham, MoTa) VALUES
(N'Bo mạch chủ', N'Nền tảng kết nối các linh kiện chính.'),
(N'Bộ xử lý (CPU)', N'Bộ não của máy tính.'),
(N'Bộ nhớ RAM', N'Bộ nhớ tạm thời tốc độ cao.'),
(N'Card đồ họa (GPU)', N'Xử lý đồ họa và xuất hình ảnh.'),
(N'Ổ cứng SSD', N'Lưu trữ dữ liệu tốc độ cao.'),
(N'Nguồn máy tính (PSU)', N'Cung cấp năng lượng cho hệ thống.'),
(N'Vỏ máy tính (Case)', N'Bảo vệ linh kiện và tản nhiệt.');
GO

-- SanPham
INSERT INTO SanPham (TenSanPham, DonGia, NgaySanXuat, ThongSoKyThuat, MaLoaiSanPham) VALUES
(N'Mainboard Gigabyte B760M DS3H DDR4', 2850000, '2023-10-01', N'Chipset Intel B760, Socket LGA1700, mATX', (SELECT MaLoaiSanPham FROM LoaiSanPham WHERE TenLoaiSanPham = N'Bo mạch chủ')),
(N'CPU Intel Core i7-14700K', 9500000, '2024-01-15', N'20 Cores, 28 Threads, Max Turbo 5.5GHz', (SELECT MaLoaiSanPham FROM LoaiSanPham WHERE TenLoaiSanPham = N'Bộ xử lý (CPU)')),
(N'RAM Kingston Fury Beast 32GB DDR5 5600MHz', 3200000, '2023-12-01', N'32GB (2x16GB), DDR5, 5600MHz', (SELECT MaLoaiSanPham FROM LoaiSanPham WHERE TenLoaiSanPham = N'Bộ nhớ RAM')),
(N'VGA ASUS ROG Strix GeForce RTX 4080 Super', 30000000, '2024-02-20', N'16GB GDDR6X, 256-bit, HDMI 2.1, DisplayPort 1.4a', (SELECT MaLoaiSanPham FROM LoaiSanPham WHERE TenLoaiSanPham = N'Card đồ họa (GPU)')),
(N'SSD Samsung 990 Pro 1TB NVMe PCIe 4.0', 2500000, '2023-11-05', N'Dung lượng 1TB, Tốc độ đọc 7450 MB/s', (SELECT MaLoaiSanPham FROM LoaiSanPham WHERE TenLoaiSanPham = N'Ổ cứng SSD')),
(N'PSU Corsair RM850e 80 Plus Gold', 2200000, '2023-09-01', N'Công suất 850W, Chuẩn 80 Plus Gold, Full Modular', (SELECT MaLoaiSanPham FROM LoaiSanPham WHERE TenLoaiSanPham = N'Nguồn máy tính (PSU)')),
(N'Vỏ Case NZXT H5 Flow', 1800000, '2023-08-10', N'Mid-Tower, Kính cường lực, Tối ưu luồng khí', (SELECT MaLoaiSanPham FROM LoaiSanPham WHERE TenLoaiSanPham = N'Vỏ máy tính (Case)')),
(N'Mainboard MSI PRO Z790-P WiFi DDR4', 4500000, '2024-03-01', N'Chipset Intel Z790, Socket LGA1700, ATX', (SELECT MaLoaiSanPham FROM LoaiSanPham WHERE TenLoaiSanPham = N'Bo mạch chủ')),
(N'CPU AMD Ryzen 7 7800X3D', 9000000, '2023-09-01', N'8 Cores, 16 Threads, Max Boost 5.0GHz', (SELECT MaLoaiSanPham FROM LoaiSanPham WHERE TenLoaiSanPham = N'Bộ xử lý (CPU)'));
GO

-- ViTriDungSanPham
INSERT INTO ViTriDungSanPham (TenNganDung) VALUES
(N'Kệ Mainboard - A1'),
(N'Kệ CPU - B1'),
(N'Kệ RAM - C1'),
(N'Kệ VGA - D1'),
(N'Kệ SSD - E1'),
(N'Kệ PSU - F1'),
(N'Khu Vỏ Case - G1');
GO

-- ChiTietViTri (Số lượng tồn kho ban đầu)
INSERT INTO ChiTietViTri (MaNganDung, MaSanPham, SoLuong) VALUES
((SELECT MaNganDung FROM ViTriDungSanPham WHERE TenNganDung = N'Kệ Mainboard - A1'), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'Mainboard Gigabyte B760M DS3H DDR4'), 10),
((SELECT MaNganDung FROM ViTriDungSanPham WHERE TenNganDung = N'Kệ CPU - B1'), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'CPU Intel Core i7-14700K'), 8),
((SELECT MaNganDung FROM ViTriDungSanPham WHERE TenNganDung = N'Kệ RAM - C1'), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'RAM Kingston Fury Beast 32GB DDR5 5600MHz'), 15),
((SELECT MaNganDung FROM ViTriDungSanPham WHERE TenNganDung = N'Kệ VGA - D1'), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'VGA ASUS ROG Strix GeForce RTX 4080 Super'), 5),
((SELECT MaNganDung FROM ViTriDungSanPham WHERE TenNganDung = N'Kệ SSD - E1'), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'SSD Samsung 990 Pro 1TB NVMe PCIe 4.0'), 12),
((SELECT MaNganDung FROM ViTriDungSanPham WHERE TenNganDung = N'Kệ PSU - F1'), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'PSU Corsair RM850e 80 Plus Gold'), 7),
((SELECT MaNganDung FROM ViTriDungSanPham WHERE TenNganDung = N'Khu Vỏ Case - G1'), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'Vỏ Case NZXT H5 Flow'), 6),
((SELECT MaNganDung FROM ViTriDungSanPham WHERE TenNganDung = N'Kệ Mainboard - A1'), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'Mainboard MSI PRO Z790-P WiFi DDR4'), 7),
((SELECT MaNganDung FROM ViTriDungSanPham WHERE TenNganDung = N'Kệ CPU - B1'), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'CPU AMD Ryzen 7 7800X3D'), 10);
GO

-- NhaCungCap
INSERT INTO NhaCungCap (TenNhaCungCap, DiaChi, SDT, Email, Website) VALUES
(N'TechGlobal Distributors', N'100 Quang Trung, Hà Đông, Hà Nội', '02431234567', 'sales@techglobal.vn', 'www.techglobal.vn'),
(N'PC Parts Express', N'50 CMT8, Quận 3, TP.HCM', '02876543210', 'contact@pcpartsexpress.com', 'www.pcpartsexpress.com'),
(N'Component Wholesalers VN', N'KCN Phố Nối, Hưng Yên', '02219998888', 'info@componentwholesalers.vn', NULL);
GO

-- PhieuNhapHang (thêm vài phiếu nhập)
INSERT INTO PhieuNhapHang (NgayNhap, MaNhanVienThucHien, MaNhaCungCap) VALUES
('2024-06-20 09:00:00', (SELECT MaNhanVien FROM NhanVien WHERE HoTen = N'Phạm Văn Duy (Quản lý kho)'), (SELECT MaNhaCungCap FROM NhaCungCap WHERE TenNhaCungCap = N'TechGlobal Distributors')),
('2024-06-25 14:30:00', (SELECT MaNhanVien FROM NhanVien WHERE HoTen = N'Phạm Văn Duy (Quản lý kho)'), (SELECT MaNhaCungCap FROM NhaCungCap WHERE TenNhaCungCap = N'PC Parts Express'));
GO

-- ChiTietPhieuNhap (tăng số lượng tồn kho của các sản phẩm)
INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSanPham, SoLuong, DonGiaNhap) VALUES
((SELECT MaPhieuNhap FROM PhieuNhapHang WHERE NgayNhap = '2024-06-20 09:00:00' AND MaNhanVienThucHien = (SELECT MaNhanVien FROM NhanVien WHERE HoTen = N'Phạm Văn Duy (Quản lý kho)')), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'Mainboard Gigabyte B760M DS3H DDR4'), 5, 2700000),
((SELECT MaPhieuNhap FROM PhieuNhapHang WHERE NgayNhap = '2024-06-20 09:00:00' AND MaNhanVienThucHien = (SELECT MaNhanVien FROM NhanVien WHERE HoTen = N'Phạm Văn Duy (Quản lý kho)')), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'CPU Intel Core i7-14700K'), 3, 9000000),
((SELECT MaPhieuNhap FROM PhieuNhapHang WHERE NgayNhap = '2024-06-25 14:30:00' AND MaNhanVienThucHien = (SELECT MaNhanVien FROM NhanVien WHERE HoTen = N'Phạm Văn Duy (Quản lý kho)')), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'SSD Samsung 990 Pro 1TB NVMe PCIe 4.0'), 7, 2300000),
((SELECT MaPhieuNhap FROM PhieuNhapHang WHERE NgayNhap = '2024-06-25 14:30:00' AND MaNhanVienThucHien = (SELECT MaNhanVien FROM NhanVien WHERE HoTen = N'Phạm Văn Duy (Quản lý kho)')), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'VGA ASUS ROG Strix GeForce RTX 4080 Super'), 2, 28500000);
GO

-- HoaDon (2 hóa đơn: 1 cho khách hàng đã đăng ký, 1 cho khách vãng lai)
INSERT INTO HoaDon (MaKhachHang, TenKhachHangVangLai, SDTKhachHangVangLai, MaNhanVienLap, PhuongThucThanhToan) VALUES
((SELECT MaKhachHang FROM KhachHang WHERE HoTen = N'Đặng Thị Thuỷ'), NULL, NULL, (SELECT MaNhanVien FROM NhanVien WHERE HoTen = N'Lê Thị Hương (Thu Ngân)'), N'Chuyển khoản'); -- Khách đăng ký

INSERT INTO HoaDon (MaKhachHang, TenKhachHangVangLai, SDTKhachHangVangLai, MaNhanVienLap, PhuongThucThanhToan) VALUES
(NULL, N'Nguyễn Văn E', '0912123123', (SELECT MaNhanVien FROM NhanVien WHERE HoTen = N'Lê Thị Hương (Thu Ngân)'), N'Tiền mặt'); -- Khách vãng lai
GO

-- ChiTietHoaDon (giảm số lượng tồn kho của các sản phẩm)
-- Cho hóa đơn của Đặng Thị Thuỷ
INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, DonGiaBan) VALUES
((SELECT MaHoaDon FROM HoaDon WHERE MaKhachHang = (SELECT MaKhachHang FROM KhachHang WHERE HoTen = N'Đặng Thị Thuỷ')), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'Mainboard Gigabyte B760M DS3H DDR4'), 1, 2850000),
((SELECT MaHoaDon FROM HoaDon WHERE MaKhachHang = (SELECT MaKhachHang FROM KhachHang WHERE HoTen = N'Đặng Thị Thuỷ')), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'RAM Kingston Fury Beast 32GB DDR5 5600MHz'), 2, 3200000);

-- Cho hóa đơn của Nguyễn Văn E (vãng lai)
INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, DonGiaBan) VALUES
((SELECT MaHoaDon FROM HoaDon WHERE TenKhachHangVangLai = N'Nguyễn Văn E'), (SELECT MaSanPham FROM SanPham WHERE TenSanPham = N'SSD Samsung 990 Pro 1TB NVMe PCIe 4.0'), 1, 2500000);
GO

-- SaoLuu và PhucHoi (ví dụ)
INSERT INTO SaoLuu (NgaySaoLuu, LoaiSaoLuu, MaNguoiDungThucHien, ViTriLuuTru) VALUES
(GETDATE(), N'Full', (SELECT MaNguoiDung FROM TaiKhoanNguoiDung WHERE LoaiNguoiDung = N'Admin' AND Username = 'admin_it'), 'C:\SQLBackup\StoreDB_Full_20240627.bak');
GO

INSERT INTO PhucHoi (NgayPhucHoi, LoaiPhucHoi, MaNguoiDungThucHien, MaSaoLuu) VALUES
(GETDATE(), N'Full', (SELECT MaNguoiDung FROM TaiKhoanNguoiDung WHERE LoaiNguoiDung = N'Admin' AND Username = 'admin_it'), (SELECT MaSaoLuu FROM SaoLuu WHERE LoaiSaoLuu = N'Full' AND ViTriLuuTru LIKE '%StoreDB_Full_20240627.bak%'));
GO

PRINT N'Đã chèn dữ liệu mẫu hoàn tất.';
