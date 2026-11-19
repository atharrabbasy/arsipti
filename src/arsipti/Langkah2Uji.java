package arsipti;

import java.util.List;
import java.util.Set;

public class Langkah2Uji {
    public static void main(String[] args) {
        List<Dokumen> daftar = List.of(
            new Materi("Modul PBO", "pbo", 2025, 4.0, Set.of("pbo", "oop"), "modul", 3),
            new Materi("PPT Algoritma", "algoritma", 2024, 2.5, Set.of("algoritma"), "ppt", 1),
            new Soal("Soal UTS Dasar Pemrograman", "dasar_pemrograman", 2024, 1.2, Set.of("uts"), "uts"),
            new ReferensiTA("Sistem Presensi Berbasis QR", "pemrograman_lanjut", 2023, 6.0,
                            Set.of("presensi", "qr"), "Dr. Budi", "")
        );

        // polymorphism: satu loop, perilaku berbeda per jenis dokumen
        for (Dokumen d : daftar) {
            try {
                d.validasi();
                System.out.println("OK   " + d.getKategori() + " -> " + d.buatKodeArsip());
            } catch (ArsipException e) {
                System.out.println("GAGAL " + e.getMessage());
            }
        }

        // pelanggaran aturan khusus tiap jenis
        List<Dokumen> salah = List.of(
            new Materi("Modul X", "pbo", 2025, 4.0, Set.of(), "buku", 3),
            new Materi("Modul Y", "pbo", 2025, 4.0, Set.of(), "modul", 20),
            new Soal("Soal Z", "pbo", 2025, 1.0, Set.of(), ""),
            new ReferensiTA("TA Tanpa Pembimbing", "pemrograman_lanjut", 2023, 6.0, Set.of(), "", "")
        );
        for (Dokumen d : salah) {
            try {
                d.validasi();
                System.out.println("seharusnya gagal: " + d.getJudul());
            } catch (ArsipException e) {
                System.out.println("TOLAK " + e.getMessage());
            }
        }
    }
}