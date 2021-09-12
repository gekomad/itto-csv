// When the user clicks on the search box, we want to toggle the search dropdown
function displayToggleSearch(e) {
  e.preventDefault();
  e.stopPropagation();

  closeDropdownSearch(e);
  
  if (idx === null) {
    console.log("Building search index...");
    prepareIdxAndDocMap();
    console.log("Search index built.");
  }
  const dropdown = document.querySelector("#search-dropdown-content");
  if (dropdown) {
    if (!dropdown.classList.contains("show")) {
      dropdown.classList.add("show");
    }
    document.addEventListener("click", closeDropdownSearch);
    document.addEventListener("keydown", searchOnKeyDown);
    document.addEventListener("keyup", searchOnKeyUp);
  }
}

//We want to prepare the index only after clicking the search bar
var idx = null
const docMap = new Map()

function prepareIdxAndDocMap() {
  const docs = [  
    {
      "title": "CSV to list",
      "url": "/itto-csv/docs/csv-to-list/",
      "content": "CSV to List Trasform a CSV string to a list of type import com.github.gekomad.ittocsv.core.FromCsv._ import com.github.gekomad.ittocsv.core.ParseFailure implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default val a: List[Either[ParseFailure, Double]] = fromCsvL[Double](\"1.1,2.1,3.1\") val b: List[Either[ParseFailure, Double]] = fromCsvL[Double](\"1.1,abc,3.1\") assert(a == List(Right(1.1), Right(2.1), Right(3.1))) assert(b == List(Right(1.1), Left(ParseFailure(\"abc is not Double\")), Right(3.1))) CSV to List of Type Trasform a CSV string to a List[Either[NonEmptyList[ParseFailure], Foo]] import com.github.gekomad.ittocsv.core.FromCsv._ implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default case class Bar(a: String, b: Int) assert(fromCsv[Bar](\"abc,42\") == List(Right(Bar(\"abc\", 42)))) assert(fromCsv[Bar](\"abc,42\\r\\nfoo,24\") == List(Right(Bar(\"abc\", 42)), Right(Bar(\"foo\", 24)))) case class Foo(v: String, a: List[Int]) assert(fromCsv[Foo](\"abc,\\\"1,2,3\\\"\") == List(Right(Foo(\"abc\", List(1, 2, 3))))) CSV to List of type with LocalDateTime Trasform a CSV string to List[Either[NonEmptyList[ParseFailure], Foo]] import com.github.gekomad.ittocsv.parser.IttoCSVFormat import com.github.gekomad.ittocsv.core.Conversions._ import com.github.gekomad.ittocsv.core.FromCsv._ case class Foo(a: Int, b: java.time.LocalDateTime) implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default val l = fromCsv[Foo](\"1,2000-12-31T11:21:19\") // List[Either[NonEmptyList[ParseFailure], Foo]] assert(l == List(Right(Foo(1, java.time.LocalDateTime.parse(\"2000-12-31T11:21:19\", java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))))) CSV to List of type with custom LocalDateTime Trasform a CSV string to List[Either[NonEmptyList[ParseFailure], Foo]] case class Foo(a: Int, b: java.time.LocalDateTime) import com.github.gekomad.ittocsv.parser.IttoCSVFormat import com.github.gekomad.ittocsv.core.FromCsv._ import java.time.LocalDateTime import java.time.format.DateTimeFormatter import com.github.gekomad.ittocsv.core.ParseFailure implicit val csvFormat = IttoCSVFormat.default implicit def localDateTimeToCsv: String =&gt; Either[ParseFailure, LocalDateTime] = { case s =&gt; scala.util.Try { Right(LocalDateTime.parse(s, DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss.0\"))) }.getOrElse(Left(ParseFailure(s\"Not a LocalDataTime $s\"))) } val l = fromCsv[Foo](\"1,2000-12-31 11:21:19.0\") // List[Either[NonEmptyList[ParseFailure], Foo]] assert(l == List(Right(Foo(1, LocalDateTime.parse(\"2000-12-31 11:21:19.0\", DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss.0\"))))))"
    } ,    
    {
      "title": "Formatters",
      "url": "/itto-csv/docs/",
      "content": "Formatters The formatter determines how CSV will be generated case class IttoCSVFormat( delimeter: Char, quote: Char, recordSeparator: String, quoteEmpty: Boolean, forceQuote: Boolean, printHeader: Boolean, trim: Boolean, ignoreEmptyLines: Boolean, quoteLowerChar: Boolean) Two formatters are available: Method Description Default Formatter Tab formatter withDelimiter(Char) the separator between fields , \\t withQuote(Char) the quoteChar character ” ” withQuoteEmpty(Boolean) quotes field if empty false false withForceQuote(Boolean) quotes all fields false false withPrintHeader(Boolean) if true prints the header (method toCsvL) false false withTrim(Boolean) trims the field false false withRecordSeparator(String) the rows separator \\r\\n \\r\\n withIgnoreEmptyLines(Boolean) skips empty lines false false withQuoteLowerChar(Boolean) quotes lower chars false false It’s possible to create custom formatters editing the default ones, example: implicit val newFormatter = default.withForceQuote(true).withRecordSeparator(\"\\n\").with....."
    } ,    
    {
      "title": "FS2 stream",
      "url": "/itto-csv/docs/fs2-stream/",
      "content": "Write fs2.Stream to file case class Bar(id: String, name: String) import com.github.gekomad.ittocsv.parser.io.ToFile.csvToFileStream implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab.withPrintHeader(true).withRecordSeparator(\"\\n\") import com.github.gekomad.ittocsv.core.ToCsv._ val stream = fs2.Stream(Bar(\"A1\", \"Jack\"), Bar(\"A2\", \"Bob\")) // fs2.Stream[Pure, Bar] val filePath: String = ??? csvToFileStream(stream, filePath).unsafeRunSync() Read fs2.Stream from file import com.github.gekomad.ittocsv.core.Conversions.fromStringToLocalDateTime import com.github.gekomad.ittocsv.parser.io.FromFile.csvFromFileStream final case class Bar(id: UUID, name: String, date: LocalDateTime) implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default csvFromFileStream[Bar](filePath, skipHeader = true) .map(csvEither =&gt; println(csvEither)) .compile .drain .attempt .unsafeRunSync() match { case Left(e) =&gt; println(\"err \" + e) false case _ =&gt; true }"
    } ,    
    {
      "title": "Further examples",
      "url": "/itto-csv/docs/further-examples/",
      "content": "Further examples From CSV to Type From Type To CSV"
    } ,    
    {
      "title": "Get Header",
      "url": "/itto-csv/docs/get-header/",
      "content": "Get Header It’s possible to get the header starting from the chosen class case class Foo(i: Int, d: Double, s: Option[String], b: Boolean) import com.github.gekomad.ittocsv.core.Header._ { implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default assert(csvHeader[Foo] == \"i,d,s,b\") } { implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default.withDelimiter('|').withForceQuote(true) assert(csvHeader[Foo] == \"\"\"\"i\"|\"d\"|\"s\"|\"b\"\"\"\") }"
    } ,      
    {
      "title": "List to CSV",
      "url": "/itto-csv/docs/list-to-csv/",
      "content": "List of Type to CSV Trasform a list of case class to a CSV multirows import com.github.gekomad.ittocsv.core.ToCsv._ implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default case class Bar(a: String, b: Int) assert(toCsvL(List(Bar(\"Bar\", 42),Bar(\"Foo\", 24))) == \"a,b\\r\\nBar,42\\r\\nFoo,24\") List of type with LocalDateTime to CSV import com.github.gekomad.ittocsv.core.ToCsv._ import java.time.LocalDateTime import com.github.gekomad.ittocsv.core.CsvStringEncoder import java.time.format.DateTimeFormatter implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default.withPrintHeader(false) val myFormatter=DateTimeFormatter.ofPattern(\"yyyy ** MM ** dd HH ++ mm ++ ss\") implicit def localDateTimeEncoder(implicit csvFormat: com.github.gekomad.ittocsv.parser.IttoCSVFormat): CsvStringEncoder[LocalDateTime] = (value: LocalDateTime) =&gt; value.format(myFormatter) case class Bar(a: String, b: Long, c: java.time.LocalDateTime, e: Option[Int]) val myDate = java.time.LocalDateTime.parse(\"2000 ** 12 ** 31 11 ++ 21 ++ 19\", myFormatter) val l: List[Bar] = List(Bar(\"Yel,low\", 3L, myDate, Some(1)), Bar(\"eee\", 7L, myDate, None)) assert(toCsvL(l) == \"\\\"Yel,low\\\",3,2000 ** 12 ** 31 11 ++ 21 ++ 19,1\\r\\neee,7,2000 ** 12 ** 31 11 ++ 21 ++ 19,\") List of type with custom LocalDateTime to CSV import java.time.LocalDateTime import com.github.gekomad.ittocsv.core.CsvStringEncoder import java.time.format.DateTimeFormatter import com.github.gekomad.ittocsv.core.ToCsv._ implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default.withPrintHeader(false) implicit def localDateTimeEncoder(implicit csvFormat: com.github.gekomad.ittocsv.parser.IttoCSVFormat): CsvStringEncoder[LocalDateTime] = (value: LocalDateTime) =&gt; value.format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss.0\")) val localDateTime = LocalDateTime.parse(\"2000-11-11 11:11:11.0\", DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss.0\")) case class Bar(a: String, b: Long, c: LocalDateTime, e: Option[Int]) val l: List[Bar] = List( Bar(\"Yel,low\", 3L, localDateTime, Some(1)), Bar(\"eee\", 7L, localDateTime, None) ) assert(toCsv(l) == \"\\\"Yel,low\\\",3,2000-11-11 11:11:11.0,1,eee,7,2000-11-11 11:11:11.0,\")"
    } ,      
    {
      "title": "Regex",
      "url": "/itto-csv/docs/regex/",
      "content": "Encode/Decode some regex import java.util.UUID import com.github.gekomad.ittocsv.core.Types.implicits._ implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default case class Bar(a: String, b: SHA1, c: SHA256, d: MD5, e: UUID, f: Email, g: IP6, h: BitcoinAdd, i: URL) val sha1 = SHA1(\"1c18da5dbf74e3fc1820469cf1f54355b7eec92d\") val uuid = UUID.fromString(\"1CC3CCBB-C749-3078-E050-1AACBE064651\") val md5 = MD5(\"23f8e84c1f4e7c8814634267bd456194\") val sha256 = SHA256(\"000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1\") val email = Email(\"daigoro@itto.com\") val ip = IP6(\"2001:db8:a0b:12f0::1\") val bitcoinAdd = BitcoinAdd(\"3Nxwenay9Z8Lc9JBiywExpnEFiLp6Afp8v\") val url = URL(\"http://www.aaa.cdd.com\") val bar = Bar(\"abc\", sha1, sha256, md5, uuid, email, ip, bitcoinAdd, url) val csvString = \"abc,1c18da5dbf74e3fc1820469cf1f54355b7eec92d,000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1,23f8e84c1f4e7c8814634267bd456194,1cc3ccbb-c749-3078-e050-1aacbe064651,daigoro@itto.com,2001:db8:a0b:12f0::1,3Nxwenay9Z8Lc9JBiywExpnEFiLp6Afp8v,http://www.aaa.cdd.com\" //encode import com.github.gekomad.ittocsv.core.ToCsv._ assert(toCsv(bar) == csvString) //decode import com.github.gekomad.ittocsv.core.FromCsv._ assert(fromCsv[Bar](csvString) == List(Right(bar))) Encode/Decode your own regex Using encode for MyType: implicit def _e(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[MyType] = ??? Example encoding N:Int to “[N]” import com.github.gekomad.ittocsv.core.CsvStringEncoder import com.github.gekomad.ittocsv.parser.IttoCSVFormat implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default case class MyType(a: Int) case class Foo(a: MyType, b: Int) import com.github.gekomad.ittocsv.core.ToCsv._ implicit def _e(implicit csvFormat: IttoCSVFormat): CsvStringEncoder[MyType] = createEncoder { node =&gt; csvConverter.stringToCsvField(s\"[${node.a}]\") } assert(toCsv(Foo(MyType(42),99)) == \"[42],99\") Using decode for MyType: implicit def _d(implicit csvFormat: IttoCSVFormat): String =&gt; Either[ParseFailure, MyType] = (str: String) =&gt; ??? Example decoding “[N]” to N:Int import com.github.gekomad.ittocsv.parser.IttoCSVFormat import scala.util.Try implicit val csvFormat: IttoCSVFormat = IttoCSVFormat.default import com.github.gekomad.ittocsv.core.ParseFailure import cats.data.NonEmptyList case class MyType(a: Int) case class Foo(a: MyType, b: Int) import com.github.gekomad.ittocsv.core.FromCsv._ implicit def _d(implicit csvFormat: IttoCSVFormat): String =&gt; Either[ParseFailure, MyType] = { str: String =&gt; if (str.startsWith(\"[\") &amp;&amp; str.endsWith(\"]\")) Try(str.substring(1, str.length - 1).toInt) .map(f =&gt; Right(MyType(f))) .getOrElse(Left(ParseFailure(s\"Not a MyType $str\"))) else Left(ParseFailure(s\"Wrong format $str\")) } assert(fromCsv[Foo](\"[42],99\") == List(Right(Foo(MyType(42),99)))) assert(fromCsv[Foo](\"[x],99\") == List(Left(NonEmptyList(ParseFailure(\"Not a MyType [x]\"), Nil)))) assert(fromCsv[Foo](\"42,99\") == List(Left(NonEmptyList(ParseFailure(\"Wrong format 42\"), Nil)))) The TreeTest.scala shows how to encode/decode a Tree[Int] Defined regex Email Email ($abc@&lt;/span&gt;def&lt;/span&gt;.c) Email1 (abc@&lt;/span&gt;def&lt;/span&gt;.com) Email simple ($@&lt;/span&gt;%&lt;/span&gt;.$) Ciphers UUID (1CC3CCBB-C749-3078-E050-1AACBE064651) MD5 (23f8e84c1f4e7c8814634267bd456194) SHA1 (1c18da5dbf74e3fc1820469cf1f54355b7eec92d) SHA256 (000020f89134d831f48541b2d8ec39397bc99fccf4cc86a3861257dbe6d819d1) URL, IP, MAC Address IP (10.192.168.1) IP_6 (2001:db8:a0b:12f0::1) URLs (http://&lt;/span&gt;abc.def&lt;/span&gt;.com) Youtube (https://&lt;/span&gt;www&lt;/span&gt;.youtube&lt;/span&gt;.com/watch?v=9bZkp7q19f0) Facebook (https://&lt;/span&gt;www&lt;/span&gt;.facebook.&lt;/span&gt;com/thesimpsons - https://&lt;/span&gt;www&lt;/span&gt;.facebook.&lt;/span&gt;com/pages/) Twitter (https://&lt;/span&gt;twitter&lt;/span&gt;.com/rtpharry) MAC Address (fE:dC:bA:98:76:54) HEX HEX (#F0F0F0 - 0xF0F0F0) Bitcoin Bitcon Address (3Nxwenay9Z8Lc9JBiywExpnEFiLp6Afp8v) Phone numbers US phone number (555-555-5555 - (416)555-3456) Italian Mobile Phone (+393471234561 - 3381234561) Italian Phone (02 645566 - 02/583725 - 02-583725) Date time 24 Hours time (23:50:00) LocalDateTime (2000-12-31T11:21:19) LocalDate (2000-12-31) LocalTime (11:21:19) OffsetDateTime (2011-12-03T10:15:30+01:00) OffsetTime (10:15:30+01:00) ZonedDateTime (2016-12-02T11:15:30-05:00) MDY (1/12/1902 - 12/31/1902) MDY2 (1-12-1902) MDY3 (01/01/1900 - 12/31/9999) MDY4 (01-12-1902 - 12-31-2018) DMY (1/12/1902) DMY2 (12-31-1902 - 1-12-1902) DMY3 (01/12/1902 - 01/12/1902) DMY4 (01-12-1902 - 01-12-1902) Time (8am - 8 pm - 11 PM - 8:00 am) Crontab Crontab expression (5 4 * * *) Codes Italian fiscal code (BDAPPP14A01A001R) Italian VAT code (13297040362) Italian Iban (IT28 W800 0000 2921 0064 5211 151 - IT28W8000000292100645211151) US states (FL - CA) US states1 (Connecticut - Colorado) US zip code (43802) US streets (123 Park Ave Apt 123 New York City, NY 10002) US street numbers (P.O. Box 432) Italian zip code (23887) German streets (Mühlenstr. 33) Concurrency USD Currency ($1.00 - 1,500.00) EUR Currency (0,00 € - 133,89 EUR - 133,89 EURO) YEN Currency (¥1.00 - 15.00 - ¥-1213,120.00) Strings Not ASCII (テスト。) Single char ASCII (A) A-Z string (abc) String and number (a1) ASCII string (a1%) Logs Apache error ([Fri Dec 16 02:25:55 2005] [error] [client 1.2.3.4] Client sent malformed Host header) Numbers Number1 (99.99 - 1.1 - .99) Unsigned32 (0 - 122 - 4294967295) Signed (-10 - +122 - 99999999999999999999999999) Percentage (10%) Scientific (-2.384E-03) Single number (1) Celsius (-2.2 °C) Fahrenheit (-2.2 °F) Coordinates Coordinate (N90.00.00 E180.00.00) Coordinate1 (45°23’36.0” N 10°33’48.0” E) Coordinate2 (12:12:12.223546”N - 15:17:6”S - 12°30’23.256547”S) Programming Comments (/* foo */)"
    } ,      
    {
      "title": "Spooling CSV file using FS2 Stream and Doobie",
      "url": "/itto-csv/docs/spooling-csvfile-using-fs2-stream-and-doobie/",
      "content": "Spooling CSV file using FS2 Stream and Doobie See Doobie Recepies project"
    } ,    
    {
      "title": "Type to CSV",
      "url": "/itto-csv/docs/type-to-csv/",
      "content": "Type to CSV Trasform a case class to CSV import com.github.gekomad.ittocsv.core.ToCsv._ implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default case class Bar(a: String, b: Int) assert(toCsv(Bar(\"侍\", 42)) == \"侍,42\") import com.github.gekomad.ittocsv.core.ToCsv._ implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default case class Baz(x: String) case class Foo(a: Int, c: Baz) case class Xyz(a: String, b: Int, c: Foo) assert(toCsv(Xyz(\"hello\", 3, Foo(1, Baz(\"hi, dude\")))) == \"hello,3,1,\\\"hi, dude\\\"\") import com.github.gekomad.ittocsv.core.ToCsv._ implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.default assert(toCsv(List(1.1, 2.1, 3.1)) == \"1.1,2.1,3.1\")"
    } ,    
    {
      "title": "Write/Read List to file",
      "url": "/itto-csv/docs/write-list-to-file/",
      "content": "Write List to file case class Bar(id: String, name: String) import com.github.gekomad.ittocsv.parser.io.ToFile.csvToFile implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab.withPrintHeader(true).withRecordSeparator(\"\\n\") import com.github.gekomad.ittocsv.core.ToCsv._ val list = List(Bar(\"A1\",\"Jack\"),Bar(\"A2\",\"Bob\")) val filePath: String = ??? csvToFile(list, filePath) Read List from file import com.github.gekomad.ittocsv.parser.io.FromFile.csvFromFileUnsafe implicit val csvFormat = com.github.gekomad.ittocsv.parser.IttoCSVFormat.tab case class Bar(id: String, name: String, date: String) val path: String = ??? val list = csvFromFileUnsafe[Bar](path, skipHeader = true) // Try[List[Either[NonEmptyList[ParseFailure], Bar]]]"
    }    
  ];

  idx = lunr(function () {
    this.ref("title");
    this.field("content");

    docs.forEach(function (doc) {
      this.add(doc);
    }, this);
  });

  docs.forEach(function (doc) {
    docMap.set(doc.title, doc.url);
  });
}

// The onkeypress handler for search functionality
function searchOnKeyDown(e) {
  const keyCode = e.keyCode;
  const parent = e.target.parentElement;
  const isSearchBar = e.target.id === "search-bar";
  const isSearchResult = parent ? parent.id.startsWith("result-") : false;
  const isSearchBarOrResult = isSearchBar || isSearchResult;

  if (keyCode === 40 && isSearchBarOrResult) {
    // On 'down', try to navigate down the search results
    e.preventDefault();
    e.stopPropagation();
    selectDown(e);
  } else if (keyCode === 38 && isSearchBarOrResult) {
    // On 'up', try to navigate up the search results
    e.preventDefault();
    e.stopPropagation();
    selectUp(e);
  } else if (keyCode === 27 && isSearchBarOrResult) {
    // On 'ESC', close the search dropdown
    e.preventDefault();
    e.stopPropagation();
    closeDropdownSearch(e);
  }
}

// Search is only done on key-up so that the search terms are properly propagated
function searchOnKeyUp(e) {
  // Filter out up, down, esc keys
  const keyCode = e.keyCode;
  const cannotBe = [40, 38, 27];
  const isSearchBar = e.target.id === "search-bar";
  const keyIsNotWrong = !cannotBe.includes(keyCode);
  if (isSearchBar && keyIsNotWrong) {
    // Try to run a search
    runSearch(e);
  }
}

// Move the cursor up the search list
function selectUp(e) {
  if (e.target.parentElement.id.startsWith("result-")) {
    const index = parseInt(e.target.parentElement.id.substring(7));
    if (!isNaN(index) && (index > 0)) {
      const nextIndexStr = "result-" + (index - 1);
      const querySel = "li[id$='" + nextIndexStr + "'";
      const nextResult = document.querySelector(querySel);
      if (nextResult) {
        nextResult.firstChild.focus();
      }
    }
  }
}

// Move the cursor down the search list
function selectDown(e) {
  if (e.target.id === "search-bar") {
    const firstResult = document.querySelector("li[id$='result-0']");
    if (firstResult) {
      firstResult.firstChild.focus();
    }
  } else if (e.target.parentElement.id.startsWith("result-")) {
    const index = parseInt(e.target.parentElement.id.substring(7));
    if (!isNaN(index)) {
      const nextIndexStr = "result-" + (index + 1);
      const querySel = "li[id$='" + nextIndexStr + "'";
      const nextResult = document.querySelector(querySel);
      if (nextResult) {
        nextResult.firstChild.focus();
      }
    }
  }
}

// Search for whatever the user has typed so far
function runSearch(e) {
  if (e.target.value === "") {
    // On empty string, remove all search results
    // Otherwise this may show all results as everything is a "match"
    applySearchResults([]);
  } else {
    const tokens = e.target.value.split(" ");
    const moddedTokens = tokens.map(function (token) {
      // "*" + token + "*"
      return token;
    })
    const searchTerm = moddedTokens.join(" ");
    const searchResults = idx.search(searchTerm);
    const mapResults = searchResults.map(function (result) {
      const resultUrl = docMap.get(result.ref);
      return { name: result.ref, url: resultUrl };
    })

    applySearchResults(mapResults);
  }

}

// After a search, modify the search dropdown to contain the search results
function applySearchResults(results) {
  const dropdown = document.querySelector("div[id$='search-dropdown'] > .dropdown-content.show");
  if (dropdown) {
    //Remove each child
    while (dropdown.firstChild) {
      dropdown.removeChild(dropdown.firstChild);
    }

    //Add each result as an element in the list
    results.forEach(function (result, i) {
      const elem = document.createElement("li");
      elem.setAttribute("class", "dropdown-item");
      elem.setAttribute("id", "result-" + i);

      const elemLink = document.createElement("a");
      elemLink.setAttribute("title", result.name);
      elemLink.setAttribute("href", result.url);
      elemLink.setAttribute("class", "dropdown-item-link");

      const elemLinkText = document.createElement("span");
      elemLinkText.setAttribute("class", "dropdown-item-link-text");
      elemLinkText.innerHTML = result.name;

      elemLink.appendChild(elemLinkText);
      elem.appendChild(elemLink);
      dropdown.appendChild(elem);
    });
  }
}

// Close the dropdown if the user clicks (only) outside of it
function closeDropdownSearch(e) {
  // Check if where we're clicking is the search dropdown
  if (e.target.id !== "search-bar") {
    const dropdown = document.querySelector("div[id$='search-dropdown'] > .dropdown-content.show");
    if (dropdown) {
      dropdown.classList.remove("show");
      document.documentElement.removeEventListener("click", closeDropdownSearch);
    }
  }
}
