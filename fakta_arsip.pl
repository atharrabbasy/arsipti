:- dynamic matkul/1, dokumen_matkul/2, kategori_dokumen/2, disetujui/1, sedang_ambil/2, pernah_unduh/2.

% matkul/1
matkul(dasar_pemrograman).
matkul(algoritma).
matkul(pbo).
matkul(struktur_data).
matkul(basis_data).
matkul(pemrograman_lanjut).

% dokumen_matkul/2
dokumen_matkul(d01, dasar_pemrograman).
dokumen_matkul(d02, dasar_pemrograman).
dokumen_matkul(d03, dasar_pemrograman).
dokumen_matkul(d04, algoritma).
dokumen_matkul(d05, algoritma).
dokumen_matkul(d06, pbo).
dokumen_matkul(d07, pbo).
dokumen_matkul(d08, struktur_data).
dokumen_matkul(d09, basis_data).
dokumen_matkul(d10, dasar_pemrograman).

% kategori_dokumen/2
Kategori_dokumen(d01, modul).
Kategori_dokumen(d02, ppt).
Kategori_dokumen(d03, soal).
Kategori_dokumen(d04, modul).
Kategori_dokumen(d05, soal).
Kategori_dokumen(d06, ppt).
Kategori_dokumen(d07, modul).
Kategori_dokumen(d08, ppt).
Kategori_dokumen(d09, modul).
Kategori_dokumen(d10, modul).

% disetujui/1 (hanya dokumen berstatus DISETUJUI)
disetujui(d01).
disetujui(d02).
disetujui(d03).
disetujui(d04).
disetujui(d05).
disetujui(d06).
disetujui(d07).
disetujui(d08).
disetujui(d09).
disetujui(d10).

% sedang_ambil/2
sedang_ambil(m001, pbo).
sedang_ambil(m002, dasar_pemrograman).

% pernah_unduh/2
pernah_unduh(m001, d10).
