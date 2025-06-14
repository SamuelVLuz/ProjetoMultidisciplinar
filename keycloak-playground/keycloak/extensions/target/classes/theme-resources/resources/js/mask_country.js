var phoneInputID = "#mobileNumberField";
var hiddenPhoneInputID = "#hiddenMobileNumberField";
var hiddenMobileNumberCountry = "#hiddenMobileNumberCountry";

const input = document.querySelector(phoneInputID);
const iti = window.intlTelInput(input, {
    autoPlaceholder: false,
    initialCountry: "br",
    separateDialCode: true,
    strictMode: true,
    i18n: {
        ad: "Andorra",
        ae: "Emirados Árabes Unidos",
        af: "Afeganistão",
        ag: "Antígua e Barbuda",
        ai: "Anguila",
        al: "Albânia",
        am: "Armênia",
        ao: "Angola",
        ar: "Argentina",
        as: "Samoa Americana",
        at: "Áustria",
        au: "Austrália",
        aw: "Aruba",
        ax: "Ilhas Aland",
        az: "Azerbaijão",
        ba: "Bósnia e Herzegovina",
        bb: "Barbados",
        bd: "Bangladesh",
        be: "Bélgica",
        bf: "Burquina Faso",
        bg: "Bulgária",
        bh: "Bahrein",
        bi: "Burundi",
        bj: "Benin",
        bl: "São Bartolomeu",
        bm: "Bermudas",
        bn: "Brunei",
        bo: "Bolívia",
        bq: "Países Baixos Caribenhos",
        br: "Brasil",
        bs: "Bahamas",
        bt: "Butão",
        bw: "Botsuana",
        by: "Bielorrússia",
        bz: "Belize",
        ca: "Canadá",
        cc: "Ilhas Cocos (Keeling)",
        cd: "Congo - Kinshasa",
        cf: "República Centro-Africana",
        cg: "República do Congo",
        ch: "Suíça",
        ci: "Costa do Marfim",
        ck: "Ilhas Cook",
        cl: "Chile",
        cm: "Camarões",
        cn: "China",
        co: "Colômbia",
        cr: "Costa Rica",
        cu: "Cuba",
        cv: "Cabo Verde",
        cw: "Curaçao",
        cx: "Ilha Christmas",
        cy: "Chipre",
        cz: "Tchéquia",
        de: "Alemanha",
        dj: "Djibuti",
        dk: "Dinamarca",
        dm: "Dominica",
        do: "República Dominicana",
        dz: "Argélia",
        ec: "Equador",
        ee: "Estônia",
        eg: "Egito",
        eh: "Saara Ocidental",
        er: "Eritreia",
        es: "Espanha",
        et: "Etiópia",
        fi: "Finlândia",
        fj: "Fiji",
        fk: "Ilhas Malvinas",
        fm: "Micronésia",
        fo: "Ilhas Faroe",
        fr: "França",
        ga: "Gabão",
        gb: "Reino Unido",
        gd: "Granada",
        ge: "Geórgia",
        gf: "Guiana Francesa",
        gg: "Guernsey",
        gh: "Gana",
        gi: "Gibraltar",
        gl: "Groenlândia",
        gm: "Gâmbia",
        gn: "Guiné",
        gp: "Guadalupe",
        gq: "Guiné Equatorial",
        gr: "Grécia",
        gt: "Guatemala",
        gu: "Guam",
        gw: "Guiné-Bissau",
        gy: "Guiana",
        hk: "Hong Kong, RAE da China",
        hn: "Honduras",
        hr: "Croácia",
        ht: "Haiti",
        hu: "Hungria",
        id: "Indonésia",
        ie: "Irlanda",
        il: "Israel",
        im: "Ilha de Man",
        in: "Índia",
        io: "Território Britânico do Oceano Índico",
        iq: "Iraque",
        ir: "Irã",
        is: "Islândia",
        it: "Itália",
        je: "Jersey",
        jm: "Jamaica",
        jo: "Jordânia",
        jp: "Japão",
        ke: "Quênia",
        kg: "Quirguistão",
        kh: "Camboja",
        ki: "Quiribati",
        km: "Comores",
        kn: "São Cristóvão e Névis",
        kp: "Coreia do Norte",
        kr: "Coreia do Sul",
        kw: "Kuwait",
        ky: "Ilhas Cayman",
        kz: "Cazaquistão",
        la: "Laos",
        lb: "Líbano",
        lc: "Santa Lúcia",
        li: "Liechtenstein",
        lk: "Sri Lanka",
        lr: "Libéria",
        ls: "Lesoto",
        lt: "Lituânia",
        lu: "Luxemburgo",
        lv: "Letônia",
        ly: "Líbia",
        ma: "Marrocos",
        mc: "Mônaco",
        md: "Moldova",
        me: "Montenegro",
        mf: "São Martinho",
        mg: "Madagascar",
        mh: "Ilhas Marshall",
        mk: "Macedônia do Norte",
        ml: "Mali",
        mm: "Mianmar (Birmânia)",
        mn: "Mongólia",
        mo: "Macau, RAE da China",
        mp: "Ilhas Marianas do Norte",
        mq: "Martinica",
        mr: "Mauritânia",
        ms: "Montserrat",
        mt: "Malta",
        mu: "Maurício",
        mv: "Maldivas",
        mw: "Malaui",
        mx: "México",
        my: "Malásia",
        mz: "Moçambique",
        na: "Namíbia",
        nc: "Nova Caledônia",
        ne: "Níger",
        nf: "Ilha Norfolk",
        ng: "Nigéria",
        ni: "Nicarágua",
        nl: "Países Baixos",
        no: "Noruega",
        np: "Nepal",
        nr: "Nauru",
        nu: "Niue",
        nz: "Nova Zelândia",
        om: "Omã",
        pa: "Panamá",
        pe: "Peru",
        pf: "Polinésia Francesa",
        pg: "Papua-Nova Guiné",
        ph: "Filipinas",
        pk: "Paquistão",
        pl: "Polônia",
        pm: "São Pedro e Miquelão",
        pr: "Porto Rico",
        ps: "Territórios palestinos",
        pt: "Portugal",
        pw: "Palau",
        py: "Paraguai",
        qa: "Catar",
        re: "Reunião",
        ro: "Romênia",
        rs: "Sérvia",
        ru: "Rússia",
        rw: "Ruanda",
        sa: "Arábia Saudita",
        sb: "Ilhas Salomão",
        sc: "Seicheles",
        sd: "Sudão",
        se: "Suécia",
        sg: "Singapura",
        sh: "Santa Helena",
        si: "Eslovênia",
        sj: "Svalbard e Jan Mayen",
        sk: "Eslováquia",
        sl: "Serra Leoa",
        sm: "San Marino",
        sn: "Senegal",
        so: "Somália",
        sr: "Suriname",
        ss: "Sudão do Sul",
        st: "São Tomé e Príncipe",
        sv: "El Salvador",
        sx: "Sint Maarten",
        sy: "Síria",
        sz: "Essuatíni",
        tc: "Ilhas Turcas e Caicos",
        td: "Chade",
        tg: "Togo",
        th: "Tailândia",
        tj: "Tadjiquistão",
        tk: "Tokelau",
        tl: "Timor-Leste",
        tm: "Turcomenistão",
        tn: "Tunísia",
        to: "Tonga",
        tr: "Turquia",
        tt: "Trinidad e Tobago",
        tv: "Tuvalu",
        tw: "Taiwan",
        tz: "Tanzânia",
        ua: "Ucrânia",
        ug: "Uganda",
        us: "Estados Unidos",
        uy: "Uruguai",
        uz: "Uzbequistão",
        va: "Cidade do Vaticano",
        vc: "São Vicente e Granadinas",
        ve: "Venezuela",
        vg: "Ilhas Virgens Britânicas",
        vi: "Ilhas Virgens Americanas",
        vn: "Vietnã",
        vu: "Vanuatu",
        wf: "Wallis e Futuna",
        ws: "Samoa",
        ye: "Iêmen",
        yt: "Mayotte",
        za: "África do Sul",
        zm: "Zâmbia",
        zw: "Zimbábue"
      }
});

input.addEventListener("countrychange", () => {
    iti.setNumber(iti.getNumber());
});

window.addEventListener("load", () => {
    iti.setNumber(iti.getNumber());
});

// Para que o número vá da forma correta para o servidor temos que adicionar ao input hidden o valor do número com DDI e DD que está em iti.getNumber(), mas para isso (como pré requisito da biblioteca) é preciso validar o número.
document.querySelector("#kc-mobile-update-form").addEventListener("submit", function() {
    if (iti.isValidNumber()) {
        $(hiddenPhoneInputID).val(iti.getNumber());
    } else {
        $(hiddenPhoneInputID).val($(phoneInputID).val());
    }

    $(hiddenMobileNumberCountry).val(iti.getSelectedCountryData().iso2);
});
