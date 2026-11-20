package arsipti;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MenuUtama {

    private final Scanner in;
    private final Arsip arsip = new Arsip();

    public MenuUtama(Scanner in) {
        this.in = in;
    }

    public static void main(String[] args) {
        new MenuUtama(new Scanner(System.in)).jalankan();
    }

    public void jalankan() {
        try {
            int pilihan;
            do {
                tampilMenu();
                pilihan = bacaPilihanMenu();
                if (pilihan == 1) {
                    unggahDokumen();
                } else if (pilihan == 2) {
                    moderasiDokumen();
                } else if (pilihan == 3) {
                    cariDanUnduh();
                } else if (pilihan == 4) {
                    tampilStatistik();
                } else if (pilihan == 5) {
                    muatDataContoh();
                } else if (pilihan == 0) {
                    System.out.println("Sampai jumpa.");
                } else if (pilihan == -1) {
                    System.out.println("Pilihan tidak tersedia.");
                }
            } while (pilihan != 0);
        } catch (IllegalStateException e) {
            System.out.println();
            System.out.println("Input berakhir. Program selesai.");
        }
    }

    private void tampilMenu() {
        System.out.println();
        System.out.println("=== ArsipTI ===");
        System.out.println("1. Unggah dokumen");
        System.out.println("2. Moderasi dokumen (admin)");
        System.out.println("3. Cari dan unduh dokumen");
        System.out.println("4. Statistik");
        System.out.println("5. Muat data contoh");
        System.out.println("0. Keluar");
    }

    private int bacaPilihanMenu() {
        String s = baca("Pilih menu: ");
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            System.out.println("Pilihan harus berupa angka.");
            return -1;
        }
    }

    private void unggahDokumen() {
        String idUser = baca("ID pengguna: ").trim();
        String kategori = baca("Kategori (modul/ppt/soal/referensi_ta): ").trim().toLowerCase();
        boolean materi = kategori.equals("modul") || kategori.equals("ppt");
        if (!materi && !kategori.equals("soal") && !kategori.equals("referensi_ta")) {
            System.out.println("Gagal: kategori tidak dikenal.");
            return;
        }

        String judul = baca("Judul: ");
        String matkul = baca("Kode mata kuliah: ");
        int tahun = inputAngka("Tahun");
        double ukuran = inputDesimal("Ukuran (MB)");
        Set<String> kunci = pisahKataKunci(baca("Kata kunci (pisahkan dengan koma): "));

        Dokumen d;
        if (materi) {
            int minggu = inputAngka("Minggu");
            d = new Materi(judul, matkul, tahun, ukuran, kunci, kategori, minggu);
        } else if (kategori.equals("soal")) {
            String jenisUjian = baca("Jenis ujian (UTS/UAS): ");
            d = new Soal(judul, matkul, tahun, ukuran, kunci, jenisUjian);
        } else {
            String pembimbing = baca("Dosen pembimbing: ");
            String abstrak = baca("Abstral (boleh kosong): ");
            d = new ReferensiTA(judul, matkul, tahun, ukuran, kunci, pembimbing, abstrak);
        }

        try {
            String id = arsip.unggah(idUser, d);
            System.out.println("Berhasil. ID dokumen " + id +" (status " + d.getStatus() + "), kode arsip " + d.buatKodeArsip());
        } catch (ArsipException e) {
            System.out.println("Gagagl: " + e.getMessage());
        }
    }

    private void moderasiDokumen(){
        List<Dokumen> menunggu = arsip.semuaDokumen().stream()
                .filter(d -> d.getStatus() == Dokumen.Status.MENUNGGU)
                .collect(Collectors.toList());
        if (menunggu.isEmpty()) {
            System.out.println("Tidak ada dokumen yang menunggu moderasi.");
            return;
        }
        System.out.println("Dokumen menunggu moderasi:");
        tampilDaftar(menunggu);

        String idAdmin = baca("ID admin: ").trim();
        String idDok = baca("ID dokumen: ").trim();
        String keputusan = baca("Keputusan (s = setujui, t = tolak): ").trim().toLowerCase();
        if (!keputusan.equals("s") && keputusan.equals("t")) {
            System.out.println("Gagal: keputusan harus s atau t.");
            return;
        }
        try {
            arsip.moderasi(idAdmin, idDok, keputusan.equals("s"));
            System.out.println("Berhasil. Dokumen " + idDok + " sekarang " + arsip.cariId(idDok).getStatus());
        } catch (ArsipException e) {
            System.out.println("Gagal: " + e.getMessage());
        }
    }
    private void cariDanUnduh() {
        String idUser = baca("ID pengguna: ").trim();
        if (arsip.cariPengguna(idUser) == null){
            System.out.println("Gagal: pengguna " + idUser + " tidak terdaftar.");
            return;
        }
        String kata = baca("Kata kunci (Kosong = semua): ");
        String kategori = baca("Kategori (Kosong = semua): ");
        int tahunMin = inputTahunMinimum();

        List<Dokumen> hasil = arsip.cari(kata, kategori, tahunMin);
        if (hasil.isEmpty()) {
            System.out.println("Tidak ada dokumen yang sesuai.");
            return;
        }
        System.out.println("Hasil pencarian (tahun terbaru dulu):");
        tampilDaftar(hasil);

        String idDok = baca("ID dokumen yang diunduh (kosong = lewati): ").trim();
        if (idDok.isEmpty()) {
            return;
        }
        try{
            arsip.unduh(idUser, idDok);
            System.out.println("Berhasil mengunduh " + idDok + " (total unduhan "
                + arsip.cariId(idDok).getJumlahUnduh() + ").");
        } catch (ArsipException e) {
            System.out.println("Gagal: " + e.getMessage());
        }
    }

    private void tampilStatistik() {
        Map<String, Long> perKategori = arsip.jumlahPerKategori();
        if (perKategori.isEmpty()) {
            System.out.println("Belum ada dokumen yang disetujui.");
            
        } else {
            System.out.println("Jumlah dokumen per kategori:");
            for (Map.Entry<String, Long> e : perKategori.entrySet()) {
                System.out.println(" " + e.getKey() + " : " + e.getValue());
            }
            List<Dokumen> top = arsip.terpopuler(3);
            if (top.isEmpty()) {
                System.out.println("Belum ada dokumen yang diunduh.");
            } else {
                System.out.println("Dokumen terpopuler:");
                for (int i = 0; i < top.size(); i++) {
                    Dokumen d = top.get(i);
                    System.out.println(" " + (i + 1) + ". " + d.getId() + " " + d.getJudul()  + " (" + d.getJumlahUnduh() + " unduhan)");
                }
            }
        }
        cetakFakta();
    }

    private void cetakFakta() {
        List<String> fakta = arsip.faktaProlog();
        System.out.println();
        System.out.println("Fakta Prolog (prasyarat/2 ditulis manual dii knowledge_base.pl:");
        for (String baris : fakta) {
            System.out.println(baris);
        }
        try {
            Files.write(Path.of("fakta_arsip.pl"), fakta, StandardCharsets.UTF_8);
            System.out.println();
            System.out.println("Fakta juga disimpan ke file fakta_arsip.pl.");
        } catch (IOException e) {
            System.out.println("Fakta tidak dapat disimpan ke file: " + e.getMessage());
        }
    }

    private void muatDataContoh() {
        if (arsip.cariPengguna("a1001") != null) {
            System.out.println("Data contoh sudah dimuat.");
            return;
        }
        try {
            for (String m : new String[]{"dasar_pemrograman", "algoritma", "pbo",
                    "struktur_data", "basis_data", "pemrograman_lanjut"}) {
                arsip.tambahKodeMatkul(m);
            }
            arsip.daftarkanPengguna(new Pengguna("m001", "Dina", "mahasiswa", Set.of("pbo")));
            arsip.daftarkanPengguna(new Pengguna("m002", "Eko", "mahasiswa", Set.of("dasar_pemrograman")));
            arsip.daftarkanPengguna(new Pengguna("k001", "Raka", "kontributor", Set.of()));
            arsip.daftarkanPengguna(new Pengguna("a001", "Bu Sari", "admin", Set.of()));

            arsip.unggah("k001", new Materi("Modul Dasar Pemrograman", "dasar_pemrograman", 2023, 4.0, Set.of("dasar", "pemrograman"), "modul", 1));
            arsip.unggah("k001", new Materi("PPT Dasar Pemrograman", "dasar_pemrograman", 2024, 2.5, Set.of("dasar", "pemrograman"), "ppt", 1));
            arsip.unggah("k001", new Soal("Soal UTS Dasar Pemrograman", "dasar_pemrograman", 2024, 1.2, Set.of("uts"), "UTS"));
            arsip.unggah("k001", new Materi("Modul Algoritma", "algoritma", 2024, 3.0, Set.of("algoritma"), "modul", 2));
            arsip.unggah("k001", new Soal("Soal UAS Algoritma", "algoritma", 2025, 1.5, Set.of("uas", "algoritma"), "UAS"));
            arsip.unggah("k001", new Materi("PPT PBO", "pbo", 2024, 2.8, Set.of("pbo", "oop"), "ppt", 1));
            arsip.unggah("k001", new Materi("Modul PBO", "pbo", 2025, 4.2, Set.of("pbo", "oop"), "modul", 3));
            arsip.unggah("k001", new Materi("PPT Struktur Data", "struktur_data", 2024, 3.1, Set.of("struktur data"), "ppt", 2));
            arsip.unggah("k001", new Materi("Modul Basis Data", "basis_data", 2025, 3.6, Set.of("basis data", "sql"), "modul", 4));

            for (int i = 1; i <= 9; i++) {
                arsip.moderasi("a001", String.format("d%02d", i), true);
            } 
        } catch (ArsipException e) {
            System.out.println("Gagal memuat data contoh: " + e.getMessage());
            return;
        }
        System.out.println("Data contoh dimuat: 6 mata kuliah, 4 pengguna, 9 dokumen (sudah disetujui).");
        System.out.println("Pengguna: m001 Dina (mahasiswa), m002 Eko (mahasiswa), k001 Raka (kontributor), a001 Bu sari (admin).");
    }

    private String baca(String prompt) {
        System.out.println(prompt);
        if (!in.hasNextLine()) {
            throw new IllegalStateException("Input berakhir.");
        }
        return in.nextLine();
    }

    private int inputAngka(String label) {
        while (true) {
            String s = baca(label + ": ");
            try {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException e) {
                System.out.println(label+ " harus berupa angka.");
            }
        }
    }

    private double inputDesimal(String label) {
        while (true) {
            String s = baca(label + ": ");
            try {
                return Double.parseDouble(s.trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println(label + " harus berupa angka.");
            }
        }
            
    }

    private int inputTahunMinimum() {
        while (true) {
            String s = baca("Tahun minimum (kosong = semua): ").trim();
            if (s.isEmpty()) {
                return 0;
            }
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Tahun harus berupa angka.");
            }
        }
    }

    private Set<String> pisahKataKunci(String teks) {
        return new HashSet<>(Arrays.asList(teks.split(",")));
    }

    private void tampilDaftar(List<Dokumen> daftar) {
        for (Dokumen d : daftar) {
        System.out.println(String.format(" %-4s %-30s %d %12s unduh: %d",  d.getId(), d.getJudul(), d.getTahun(), d.getKategori(), d.getJumlahUnduh()));
        }
    }



}