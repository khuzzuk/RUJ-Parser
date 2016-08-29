package Records;

public class RFL
{
	public static final int TYPE=0;
	public static final int SUBTYPE=1;
	public static final int TITLE=2;
	public static final int TITLE_ALT=3;
	public static final int TITLE_ORG=4;
	public static final int AUTHORS=5;
	public static final int COAUTHORS=6;
	public static final int TRANSLATORS=7;
	public static final int REVIEWER=8;
	public static final int EDITOR=9;
	public static final int ID=38;
	public static final int USOS=42;
	public static final String[][] F = new String[][] {
			{"getType","Typ","dc.type[pl]"},//0 TYPE
			{"getSubtype","Podtyp","dc.subtype[pl]"},//1
			{"", "Tytu�", "dc.title[pl]"},//2
			{"","Wariant tytu�u","dc.title.alternative[pl]"},//3
			{"","Tytu� orygina�u","dc.title.original[pl]"},//4
			{"author","Autorzy","dc.contributor.author[pl]"},//5
			{"author","Wsp�tw�rcy","dc.contributor.other[pl]"},//6
			{"author","T�umacze","dc.contributor.translator[pl]"},//7
			{"author","Recenzenci","dc.contributor.reviewer[pl]"},//8
			{"editor","Redaktorzy","dc.contributor.editor[pl]"},//9
			{"getIntitution","Instytucja sprawcza","dc.contributor.institution[pl]"},//10
			{"getTitleJournal","Tytu� czasopisma","dc.title.journal[pl]"},//11
			{"getTitleContainer","Tytu� publikacji macierzystej","dc.title.container[pl]"},//12
			{"getTitleVolume","Tytu� tomu","dc.title.volume[pl]"},//13
			{"getVolume","Tom","dc.description.volume[pl]"},//14
			{"getNumber","Numer","dc.description.number[pl]"},//15
			{"getEdition","Wydanie","dc.description.edition[pl]"},//16
			{"getPhysical","Opis fizyczny","dc.description.physical[pl]"},//17
			{"getArticleID","ID artyku�u","dc.identifier.articleid[pl]"},//18
			{"getDateIssued","Data wydania","dc.date.issued[pl]"},//19
			{"getDateCreated","Data powstania/nominalna","dc.date.created[pl]"},//20
			{"getDateSubmitted","Data zg�oszenia/obrony","dc.date.submitted[pl]"},//21
			{"getPubInfo","Adres wydawniczy","dc.pubinfo[pl]"},//22
			{"getPlace","Miejsce powstania","dc.place[pl]"},//23
			{"getSeries","Seria","dc.description.series[pl]"},
			{"isbn","ISBN","dc.identifier.isbn[pl]"},//25
			{"isbn","eISBN","dc.identifier.eisbn[pl]"},
			{"getISSN","ISSN","dc.identifier.issn[pl]"},//27
			{"geteISSN","eISSN","dc.identifier.eissn[pl]"},//28
			{"getLang","J�zyk","dc.language[pl]"},
			{"getLang2","J�zyk publ. macierzystej","dc.language.container[pl]"},
			{"getDOI","DOI","dc.identifier.doi[pl]"},//31
			{"getLink","Link","dc.identifier.weblink[pl]"},//32
			{"getAccession","Data dost�pu","dc.date.accession[pl]"},//33
			{"getAffiliation","Afiliacja","dc.affiliation[pl]"},//34
			{"getConfType","Typ konferencji","dc.description.conftype[pl]"},//35
			{"getURI","Link w bazie","dc.identifier.uri[]"},//36
			{"","Kolekcja","collection"},//37
			{"","Id","id"},//38
			{"","Arkusze wydawnicze","dc.description.publication[pl]"}, // 39
			{"conference", "Dane konferencji", "dc.conference[pl]" },//40
			{"open-access", "Informacje o otwartym dost�pie", "dc.rights.original[pl]"},//41
			{"usosID", "numer USOS", "dc.pbn.affiliation[pl]"},//42
			{"keywords pl", "s�owa kluczowe (pl)", "dc.subject.pl[pl]"},//43
			{"keywords en", "s�owa kluczowe (en)", "dc.subject.en[pl]"},//44
			{"getAdmin","Uwagi administratora [pl]","dc.description.admin[pl]"},//45 ADMIN
			{"getAdmin2","Uwagi administratora []","dc.description.admin[]"}//46 ADMIN2
			};
	public static String[][] list()
	{
		return new String[][] {
				{"", "Tytuł", "dc.title[pl]"},//0
				{"","Wariant tytułu","dc.title.alternative[pl]"},//1
				{"","Tytuł oryginału","dc.title.original[pl]"},//2
				{"author","Autorzy","dc.contributor.author[pl]"},//3
				{"author","Współtwórcy","dc.contributor.other[pl]"},//4
				{"author","Tłumacze","dc.contributor.translator[pl]"},//5
				{"author","Recenzenci","dc.contributor.reviewer[pl]"},//6
				{"getIntitution","Instytucja sprawcza","dc.contributor.institution[pl]"},//7
				{"getTitleJournal","Tytuł czasopisma","dc.title.journal[pl]"},//8
				{"getTitleContainer","Tytuł publikacji macierzystej","dc.title.container[pl]"},//9
				{"getTitleVolume","Tytuł tomu","dc.title.volume[pl]"},//10
				{"getVolume","Tom","dc.description.volume[pl]"},//11
				{"getNumber","Numer","dc.description.number[pl]"},//12
				{"getEdition","Wydanie","dc.description.edition[pl]"},//13
				{"editor","Redaktorzy","dc.contributor.editor[pl]"},//14
				{"getPhysical","Opis fizyczny","dc.description.physical[pl]"},//15
				{"getArticleID","ID artykułu","dc.identifier.articleid[pl]"},//16
				{"getDateIssued","Data wydania","dc.date.issued[pl]"},//17
				{"getDateCreated","Data powstania/nominalna","dc.date.created[pl]"},
				{"getDateSubmitted","Data zgłoszenia/obrony","dc.date.submitted[pl]"},
				{"getPubInfo","Adres wydawniczy","dc.pubinfo[pl]"},//20
				{"getPlace","Miejsce powstania","dc.place[pl]"},
				{"getSeries","Seria","dc.description.series[pl]"},
				{"isbn","ISBN","dc.identifier.isbn[pl]"},//23
				{"isbn","eISBN","dc.identifier.eisbn[pl]"},
				{"getISSN","ISSN","dc.identifier.issn[pl]"},//25
				{"geteISSN","eISSN","dc.identifier.eissn[pl]"},//26
				{"getLang","Język","dc.language[pl]"},
				{"getLang2","Język publ. macierzystej","dc.language.container[pl]"},
				{"getDOI","DOI","dc.identifier.doi[pl]"},//29
				{"getLink","Link","dc.identifier.weblink[pl]"},//30
				{"getAccession","Data dostępu","dc.date.accession[pl]"},
				{"getAffiliation","Afiliacja","dc.affiliation[pl]"},
				{"getConfType","Typ konferencji","dc.description.conftype[pl]"},
				{"getURI","Link w bazie","dc.identifier.uri[]"},
				{"getAdmin","Uwagi administratora [pl]","dc.description.admin[pl]"},//35 ADMIN
				{"getType","Typ","dc.type[pl]"},//36 TYPE
				{"getSubtype","Podtyp","dc.subtype[pl]"},
				{"","Kolekcja","collection"},//38
				{"","Id","id"},//39
				{"","Arkusze wydawnicze","dc.description.publication[pl]"}, // 40
				{"conference", "Dane konferencji", "dc.conference[pl]" },
				{"open-access", "Informacje o otwartym dostępie", "dc.rights.original[pl]"},//42
				{"usosID", "numer USOS", "dc.pbn.affiliation[pl]"},//43
				{"keywords pl", "słowa kluczowe (pl)", "dc.subject.pl[pl]"},//44
				{"keywords en", "słowa kluczowe (en)", "dc.subject.en[pl]"},//45
				{"getAdmin2","Uwagi administratora []","dc.description.admin[]"}//46 ADMIN2
				};
	}
	public static boolean checkField(String field){
		String[][] fields = list();
		for (int x=0; x<fields.length; x++){
			if (fields[x][2].equals(field)) return true;
		}
		return false;
	}
	public static String[][] list2()
	{
		String[][] methods = new String[][] {
				{"getTitle", "Tytu�", "dc.title[pl]"},
				{"getTitleAlternative","Wariant tytu�u","dc.title.alternative[pl]"},
				{"getTitleOriginal","Tytu� orygina�u","dc.title.original[pl]"},
				{"getAuthor","Autorzy","dc.contributor.author[pl]"},
				{"getContributorOther","Wsp�tw�rcy","dc.contributor.other[pl]"},
				{"getTranslator","T�umacze","dc.contributor.translator[pl]"},
				{"getReviewer","Recenzenci","dc.contributor.reviewer[pl]"},
				{"getIntitution","Instytucja sprawcza","dc.contributor.intitution[pl]"},
				{"getTitleJournal","Tytu� czasopisma","dc.title.journal[pl]"},
				{"getTitleContainer","Tytu� publikacji macierzystej","dc.title.container[pl]"},
				{"getTitleVolume","Tytu� tomu","dc.title.volume[pl]"},
				{"getVolume","Tom","dc.description.volume[pl]"},
				{"getNumber","Numer","dc.description.number[pl]"},
				{"getEdition","Wydanie","dc.description.edition[pl]"},
				{"getEditor","Redaktorzy","dc.contributor.editor[pl]"},
				{"getPhysical","Opis fizyczny","dc.description.physical[pl]"},
				{"getArticleID","ID artyku�u","dc.description.articleid[pl]"},
				{"getDateIssued","Data wydania","dc.date.issued[pl]"},
				{"getDateCreated","Data powstania/nominalna","dc.date.created[pl]"},
				{"getDateSubmitted","Data zg�oszenia/obrony","dc.date.submitted[pl]"},
				{"getPubInfo","Adres wydawniczy","dc.pubinfo[pl]"},
				{"getPlace","Miejsce powstania","dc.place"},
				{"getSeries","Seria","dc.description.series[pl]"},
				{"getISBN","ISBN","dc.identifier.isbn[pl]"},
				{"geteISBN","eISBN","dc.identifier.eisbn[pl]"},
				{"getISSN","ISSN","dc.identifier.issn[pl]"},
				{"geteISSN","eISSN","dc.identifier.eissn[pl]"},
				{"getLang","J�zyk","dc.language[pl]"},
				{"getLang2","J�zyk publ. macierzystej","dc.language.container[pl]"},
				{"getDOI","DOI","dc.identifier.doi[pl]"},
				{"getLink","Link","dc.identifier.weblink[pl]"},
				{"getAccession","Data dost�pu","dc.date.accession[pl]"},
				{"getAffiliation","Afiliacja","dc.affiliation[pl]"},
				{"getConfType","Typ konferencji","dc.conftype[pl]"},
				{"getURI","Link w bazie","dc.identifier.uri"},
				{"getAdmin","Uwagi administratora","dc.description.admin[pl]"},
				{"getType","Typ","dc.type[pl]"},
				{"getSubtype","Podtyp","dc.subtype[pl]"}
				};
		return methods;
	}
}