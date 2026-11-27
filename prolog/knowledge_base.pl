:- include('fakta_arsip.pl').

% fakta kurikulum
prasyarat(algoritma, dasar_pemrograman).
prasyarat(pbo, algoritma).
prasyarat(struktur_data, algoritma).
prasyarat(basis_data, struktur_data).
prasyarat(pemrograman_lanjut, pbo).

harus_dikuasai(MK, P) :- prasyarat(MK, P).
harus_dikuasai(MK, P) :- prasyarat(MK, Y), harus_dikuasai(Y, P).

relevan(MK, MK).
relevan(MK, P) :- harus_Dikuasai(MK, P).

saran_dokumen(M, D) :-
    sedang_ambil(M, MK),
    relevan(MK, DM),
    dokumen_matkul(D, DM),
    disetujui(D),
    kategori_dokumen(D, K),
    K \= referensi_ta,
    \+ pernah_unduh(M, D).

tanpa_arsip(MK) :-
    matkul(MK),
    \+ ( dokumen_matkul(D, MK), disetujui(D) ).