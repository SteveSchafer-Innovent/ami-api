use ami;
drop table if exists misspellings;
create table misspellings (
	incorrect varchar(32) not null primary key,
	correct varchar(32) not null
);
-- insert into misspellings (correct, incorrect) values ('a lot', 'allot');
-- insert into misspellings (correct, incorrect) values ('a lot', 'alot');
-- insert into misspellings (correct, incorrect) values ('hors d''oeuvres', 'hors derves');
-- insert into misspellings (correct, incorrect) values ('hors d''oeuvres', 'ordeurves');
insert into misspellings (correct, incorrect) values ('absence', 'abcense');
insert into misspellings (correct, incorrect) values ('absence', 'absance');
insert into misspellings (correct, incorrect) values ('absence', 'absense');
insert into misspellings (correct, incorrect) values ('absence', 'absentse');
insert into misspellings (correct, incorrect) values ('acceptable', 'acceptible');
insert into misspellings (correct, incorrect) values ('accidentally', 'accidentaly');
insert into misspellings (correct, incorrect) values ('accommodate', 'accomodate');
insert into misspellings (correct, incorrect) values ('accommodate', 'acommodate');
insert into misspellings (correct, incorrect) values ('accommodation', 'accomodation');
insert into misspellings (correct, incorrect) values ('achieve', 'acheive');
insert into misspellings (correct, incorrect) values ('acknowledge', 'acknowlege');
insert into misspellings (correct, incorrect) values ('acknowledge', 'aknowledge');
insert into misspellings (correct, incorrect) values ('acquaintance', 'acquaintence');
insert into misspellings (correct, incorrect) values ('acquaintance', 'aquaintance');
insert into misspellings (correct, incorrect) values ('acquire', 'adquire');
insert into misspellings (correct, incorrect) values ('acquire', 'aquire');
insert into misspellings (correct, incorrect) values ('acquit', 'aquit');
insert into misspellings (correct, incorrect) values ('acreage', 'acerage');
insert into misspellings (correct, incorrect) values ('acreage', 'acrage');
insert into misspellings (correct, incorrect) values ('across', 'accross');
insert into misspellings (correct, incorrect) values ('address', 'adress');
insert into misspellings (correct, incorrect) values ('adultery', 'adultary');
insert into misspellings (correct, incorrect) values ('advisable', 'adviseable');
insert into misspellings (correct, incorrect) values ('advisable', 'advizable');
insert into misspellings (correct, incorrect) values ('affect', 'effect');
insert into misspellings (correct, incorrect) values ('aggression', 'agression');
insert into misspellings (correct, incorrect) values ('aggressive', 'agressive');
insert into misspellings (correct, incorrect) values ('allegiance', 'alegiance');
insert into misspellings (correct, incorrect) values ('allegiance', 'allegaince');
insert into misspellings (correct, incorrect) values ('allegiance', 'allegience');
insert into misspellings (correct, incorrect) values ('almost', 'allmost');
insert into misspellings (correct, incorrect) values ('amateur', 'amatuer');
insert into misspellings (correct, incorrect) values ('amateur', 'amature');
insert into misspellings (correct, incorrect) values ('annually', 'annualy');
insert into misspellings (correct, incorrect) values ('annually', 'anually');
insert into misspellings (correct, incorrect) values ('apparent', 'aparent');
insert into misspellings (correct, incorrect) values ('apparent', 'aparrent');
insert into misspellings (correct, incorrect) values ('apparent', 'apparant');
insert into misspellings (correct, incorrect) values ('apparent', 'apparrent');
insert into misspellings (correct, incorrect) values ('apparently', 'apparantly');
insert into misspellings (correct, incorrect) values ('appearance', 'appearence');
insert into misspellings (correct, incorrect) values ('arctic', 'artic');
insert into misspellings (correct, incorrect) values ('argument', 'arguement');
insert into misspellings (correct, incorrect) values ('assassination', 'assasination');
insert into misspellings (correct, incorrect) values ('atheist', 'athiest');
insert into misspellings (correct, incorrect) values ('atheist', 'athist');
insert into misspellings (correct, incorrect) values ('awful', 'aweful');
insert into misspellings (correct, incorrect) values ('awful', 'awfull');
insert into misspellings (correct, incorrect) values ('basically', 'basicly');
insert into misspellings (correct, incorrect) values ('beautiful', 'beatiful');
insert into misspellings (correct, incorrect) values ('because', 'becuase');
insert into misspellings (correct, incorrect) values ('becoming', 'becomeing');
insert into misspellings (correct, incorrect) values ('beginning', 'begining');
insert into misspellings (correct, incorrect) values ('believe', 'beleive');
insert into misspellings (correct, incorrect) values ('believe', 'beleive, belive');
insert into misspellings (correct, incorrect) values ('bellwether', 'bellweather');
insert into misspellings (correct, incorrect) values ('bizarre', 'bizzare');
insert into misspellings (correct, incorrect) values ('buoy', 'bouy');
insert into misspellings (correct, incorrect) values ('buoyant', 'bouyant');
insert into misspellings (correct, incorrect) values ('business', 'buisness');
insert into misspellings (correct, incorrect) values ('calendar', 'calender');
insert into misspellings (correct, incorrect) values ('camouflage', 'camoflage');
insert into misspellings (correct, incorrect) values ('camouflage', 'camoflague');
insert into misspellings (correct, incorrect) values ('capitol', 'capital');
insert into misspellings (correct, incorrect) values ('caribbean', 'carribean');
insert into misspellings (correct, incorrect) values ('category', 'catagory');
insert into misspellings (correct, incorrect) values ('caught', 'caugt');
insert into misspellings (correct, incorrect) values ('caught', 'cauhgt');
insert into misspellings (correct, incorrect) values ('cemetery', 'cemetary');
insert into misspellings (correct, incorrect) values ('cemetery', 'cemetary,');
insert into misspellings (correct, incorrect) values ('changeable', 'changable');
insert into misspellings (correct, incorrect) values ('chauffeur', 'chauffer');
insert into misspellings (correct, incorrect) values ('chief', 'cheif');
insert into misspellings (correct, incorrect) values ('colleague', 'collaegue');
insert into misspellings (correct, incorrect) values ('colleague', 'collegue');
insert into misspellings (correct, incorrect) values ('column', 'colum');
insert into misspellings (correct, incorrect) values ('coming', 'comming');
insert into misspellings (correct, incorrect) values ('committed', 'comitted');
insert into misspellings (correct, incorrect) values ('committed', 'commited');
insert into misspellings (correct, incorrect) values ('committee', 'commitee');
insert into misspellings (correct, incorrect) values ('comparison', 'comparsion');
insert into misspellings (correct, incorrect) values ('completely', 'completly');
insert into misspellings (correct, incorrect) values ('concede', 'conceed');
insert into misspellings (correct, incorrect) values ('congratulate', 'congradulate');
insert into misspellings (correct, incorrect) values ('conscientious', 'consciencious');
insert into misspellings (correct, incorrect) values ('conscious', 'concious');
insert into misspellings (correct, incorrect) values ('conscious', 'consious');
insert into misspellings (correct, incorrect) values ('consensus', 'concensus');
insert into misspellings (correct, incorrect) values ('controversy', 'contraversy');
insert into misspellings (correct, incorrect) values ('coolly', 'cooly');
insert into misspellings (correct, incorrect) values ('curiosity', 'curiousity');
insert into misspellings (correct, incorrect) values ('daiquiri', 'dacquiri');
insert into misspellings (correct, incorrect) values ('daiquiri', 'daquiri');
insert into misspellings (correct, incorrect) values ('deceive', 'decieve');
insert into misspellings (correct, incorrect) values ('definite', 'definate,');
insert into misspellings (correct, incorrect) values ('definitely', 'definately');
insert into misspellings (correct, incorrect) values ('definitely', 'definitly,');
insert into misspellings (correct, incorrect) values ('desperate', 'desparate');
insert into misspellings (correct, incorrect) values ('difference', 'diffrence');
insert into misspellings (correct, incorrect) values ('dilemma', 'dilema');
insert into misspellings (correct, incorrect) values ('dilemma', 'dilemna');
insert into misspellings (correct, incorrect) values ('disappear', 'dissapear');
insert into misspellings (correct, incorrect) values ('disappoint', 'dissapoint');
insert into misspellings (correct, incorrect) values ('disastrous', 'disasterous');
insert into misspellings (correct, incorrect) values ('drunkenness', 'drunkeness');
insert into misspellings (correct, incorrect) values ('dumbbell', 'dumbell');
insert into misspellings (correct, incorrect) values ('ecstasy', 'ecstacy');
insert into misspellings (correct, incorrect) values ('embarrass', 'embarass');
insert into misspellings (correct, incorrect) values ('environment', 'enviroment');
insert into misspellings (correct, incorrect) values ('equipment', 'equiptment');
insert into misspellings (correct, incorrect) values ('exceed', 'excede');
insert into misspellings (correct, incorrect) values ('exhilarate', 'exilerate');
insert into misspellings (correct, incorrect) values ('existence', 'existance');
insert into misspellings (correct, incorrect) values ('experience', 'experiance');
insert into misspellings (correct, incorrect) values ('extreme', 'extreem');
insert into misspellings (correct, incorrect) values ('Fahrenheit', 'Farenheit');
insert into misspellings (correct, incorrect) values ('familiar', 'familar');
insert into misspellings (correct, incorrect) values ('fascinating', 'facinating');
insert into misspellings (correct, incorrect) values ('fiery', 'firey');
insert into misspellings (correct, incorrect) values ('finally', 'finaly');
insert into misspellings (correct, incorrect) values ('fluorescent', 'florescent');
insert into misspellings (correct, incorrect) values ('fluorescent', 'flourescent');
insert into misspellings (correct, incorrect) values ('foreign', 'foriegn');
insert into misspellings (correct, incorrect) values ('foreseeable', 'forseeable');
insert into misspellings (correct, incorrect) values ('forty', 'fourty');
insert into misspellings (correct, incorrect) values ('forward', 'foward');
insert into misspellings (correct, incorrect) values ('friend', 'freind');
insert into misspellings (correct, incorrect) values ('fulfil', 'fulfill');
insert into misspellings (correct, incorrect) values ('fulfil', 'fullfil');
insert into misspellings (correct, incorrect) values ('further', 'futher');
insert into misspellings (correct, incorrect) values ('gauge', 'guage');
insert into misspellings (correct, incorrect) values ('gist', 'jist');
insert into misspellings (correct, incorrect) values ('glamorous', 'glamourous');
insert into misspellings (correct, incorrect) values ('government', 'goverment');
insert into misspellings (correct, incorrect) values ('grateful', 'gratefull');
insert into misspellings (correct, incorrect) values ('grateful', 'greatful');
insert into misspellings (correct, incorrect) values ('great', 'grat');
insert into misspellings (correct, incorrect) values ('great', 'grate');
insert into misspellings (correct, incorrect) values ('guarantee', 'garantee');
insert into misspellings (correct, incorrect) values ('guarantee', 'garanty');
insert into misspellings (correct, incorrect) values ('guarantee', 'garentee');
insert into misspellings (correct, incorrect) values ('guard', 'gaurd');
insert into misspellings (correct, incorrect) values ('guidance', 'guidence');
insert into misspellings (correct, incorrect) values ('happened', 'happend');
insert into misspellings (correct, incorrect) values ('harass', 'harrass');
insert into misspellings (correct, incorrect) values ('harassment', 'harrassment');
insert into misspellings (correct, incorrect) values ('height', 'heighth');
insert into misspellings (correct, incorrect) values ('height', 'heigth');
insert into misspellings (correct, incorrect) values ('hierarchy', 'heirarchy');
insert into misspellings (correct, incorrect) values ('honorary', 'honourary');
insert into misspellings (correct, incorrect) values ('humorous', 'humerous');
insert into misspellings (correct, incorrect) values ('humorous', 'humourous');
insert into misspellings (correct, incorrect) values ('hygiene', 'higeine');
insert into misspellings (correct, incorrect) values ('hygiene', 'hiygeine');
insert into misspellings (correct, incorrect) values ('hygiene', 'hygeine');
insert into misspellings (correct, incorrect) values ('hygiene', 'hygene');
insert into misspellings (correct, incorrect) values ('hygiene', 'hygine');
insert into misspellings (correct, incorrect) values ('hypocrite', 'hipocrit');
insert into misspellings (correct, incorrect) values ('idiosyncrasy', 'idiosyncracy');
insert into misspellings (correct, incorrect) values ('ignorance', 'ignorence');
insert into misspellings (correct, incorrect) values ('imitate', 'immitate');
insert into misspellings (correct, incorrect) values ('immediately', 'imediately');
insert into misspellings (correct, incorrect) values ('immediately', 'immediatly');
insert into misspellings (correct, incorrect) values ('incidentally', 'incidently');
insert into misspellings (correct, incorrect) values ('independent', 'independant');
insert into misspellings (correct, incorrect) values ('indict', 'indite');
insert into misspellings (correct, incorrect) values ('indispensable', 'indispensible');
insert into misspellings (correct, incorrect) values ('inoculate', 'innoculate');
insert into misspellings (correct, incorrect) values ('intelligence', 'inteligence');
insert into misspellings (correct, incorrect) values ('intelligence', 'intelligance');
insert into misspellings (correct, incorrect) values ('interrupt', 'interupt');
insert into misspellings (correct, incorrect) values ('irresistible', 'irresistable');
insert into misspellings (correct, incorrect) values ('jewelry', 'jewelery');
insert into misspellings (correct, incorrect) values ('judgment', 'judgement');
insert into misspellings (correct, incorrect) values ('kernel', 'kernal');
insert into misspellings (correct, incorrect) values ('knowledge', 'knowlege');
insert into misspellings (correct, incorrect) values ('leisure', 'liesure');
insert into misspellings (correct, incorrect) values ('liaise', 'liase');
insert into misspellings (correct, incorrect) values ('liaison', 'liason');
insert into misspellings (correct, incorrect) values ('library', 'libary');
insert into misspellings (correct, incorrect) values ('library', 'liberry');
insert into misspellings (correct, incorrect) values ('license', 'lisence');
insert into misspellings (correct, incorrect) values ('lightning', 'lightening');
insert into misspellings (correct, incorrect) values ('lollipop', 'lollypop');
insert into misspellings (correct, incorrect) values ('lose', 'loose');
insert into misspellings (correct, incorrect) values ('maintenance', 'maintainance');
insert into misspellings (correct, incorrect) values ('maintenance', 'maintnance');
insert into misspellings (correct, incorrect) values ('medieval', 'medeval');
insert into misspellings (correct, incorrect) values ('medieval', 'medevil');
insert into misspellings (correct, incorrect) values ('medieval', 'mideval');
insert into misspellings (correct, incorrect) values ('memento', 'momento');
insert into misspellings (correct, incorrect) values ('millennia', 'millenia');
insert into misspellings (correct, incorrect) values ('millennium', 'milennium');
insert into misspellings (correct, incorrect) values ('millennium', 'millenium');
insert into misspellings (correct, incorrect) values ('miniature', 'miniture');
insert into misspellings (correct, incorrect) values ('minuscule', 'miniscule');
insert into misspellings (correct, incorrect) values ('mischievous', 'mischevious');
insert into misspellings (correct, incorrect) values ('mischievous', 'mischevous');
insert into misspellings (correct, incorrect) values ('mischievous', 'mischievious');
insert into misspellings (correct, incorrect) values ('misspell', 'mispell');
insert into misspellings (correct, incorrect) values ('misspell', 'misspel');
insert into misspellings (correct, incorrect) values ('Neanderthal', 'Neandertal');
insert into misspellings (correct, incorrect) values ('necessary', 'neccessary');
insert into misspellings (correct, incorrect) values ('necessary', 'necessery');
insert into misspellings (correct, incorrect) values ('neighbour', 'nieghbor');
insert into misspellings (correct, incorrect) values ('niece', 'neice');
insert into misspellings (correct, incorrect) values ('noticeable', 'noticable');
insert into misspellings (correct, incorrect) values ('occasion', 'ocassion, occassion');
insert into misspellings (correct, incorrect) values ('occasion', 'occassion');
insert into misspellings (correct, incorrect) values ('occasionally', 'occasionaly');
insert into misspellings (correct, incorrect) values ('occasionally', 'occassionally');
insert into misspellings (correct, incorrect) values ('occurred', 'occured');
insert into misspellings (correct, incorrect) values ('occurrence', 'occurance, occurence');
insert into misspellings (correct, incorrect) values ('occurrence', 'occurence');
insert into misspellings (correct, incorrect) values ('occurrence', 'occurrance');
insert into misspellings (correct, incorrect) values ('occurring', 'occuring');
insert into misspellings (correct, incorrect) values ('omission', 'omision');
insert into misspellings (correct, incorrect) values ('omission', 'ommision');
insert into misspellings (correct, incorrect) values ('original', 'orignal');
insert into misspellings (correct, incorrect) values ('outrageous', 'outragous');
insert into misspellings (correct, incorrect) values ('parliament', 'parliment');
insert into misspellings (correct, incorrect) values ('pastime', 'passtime');
insert into misspellings (correct, incorrect) values ('pastime', 'pasttime');
insert into misspellings (correct, incorrect) values ('pavilion', 'pavillion');
insert into misspellings (correct, incorrect) values ('perceive', 'percieve');
insert into misspellings (correct, incorrect) values ('perseverance', 'perseverence');
insert into misspellings (correct, incorrect) values ('persistent', 'persistant');
insert into misspellings (correct, incorrect) values ('personnel', 'personel');
insert into misspellings (correct, incorrect) values ('personnel', 'personell');
insert into misspellings (correct, incorrect) values ('pharaoh', 'pharoah');
insert into misspellings (correct, incorrect) values ('piece', 'peice');
insert into misspellings (correct, incorrect) values ('plagiarize', 'plagerize');
insert into misspellings (correct, incorrect) values ('playwright', 'playright');
insert into misspellings (correct, incorrect) values ('playwright', 'playwrite');
insert into misspellings (correct, incorrect) values ('politician', 'politican');
insert into misspellings (correct, incorrect) values ('Portuguese', 'Portugese');
insert into misspellings (correct, incorrect) values ('possession', 'posession');
insert into misspellings (correct, incorrect) values ('possession', 'possesion');
insert into misspellings (correct, incorrect) values ('potatoes', 'potatos');
insert into misspellings (correct, incorrect) values ('precede', 'preceed');
insert into misspellings (correct, incorrect) values ('preferred', 'prefered');
insert into misspellings (correct, incorrect) values ('preferring', 'prefering');
insert into misspellings (correct, incorrect) values ('presence', 'presance');
insert into misspellings (correct, incorrect) values ('principle', 'principal');
insert into misspellings (correct, incorrect) values ('privilege', 'privelege');
insert into misspellings (correct, incorrect) values ('privilege', 'priviledge');
insert into misspellings (correct, incorrect) values ('professor', 'professer');
insert into misspellings (correct, incorrect) values ('promise', 'promiss');
insert into misspellings (correct, incorrect) values ('pronunciation', 'pronounciation');
insert into misspellings (correct, incorrect) values ('proof', 'prufe');
insert into misspellings (correct, incorrect) values ('propaganda', 'propoganda');
insert into misspellings (correct, incorrect) values ('prophecy', 'prophesy');
insert into misspellings (correct, incorrect) values ('protester', 'protestor');
insert into misspellings (correct, incorrect) values ('publicly', 'publically');
insert into misspellings (correct, incorrect) values ('quarantine', 'quarentine');
insert into misspellings (correct, incorrect) values ('questionnaire', 'questionaire');
insert into misspellings (correct, incorrect) values ('questionnaire', 'questionnair');
insert into misspellings (correct, incorrect) values ('queue', 'que');
insert into misspellings (correct, incorrect) values ('readable', 'readible');
insert into misspellings (correct, incorrect) values ('really', 'realy');
insert into misspellings (correct, incorrect) values ('receipt', 'reciept');
insert into misspellings (correct, incorrect) values ('receive', 'recieve');
insert into misspellings (correct, incorrect) values ('recommend', 'reccommend');
insert into misspellings (correct, incorrect) values ('recommend', 'recomend');
insert into misspellings (correct, incorrect) values ('reference', 'referance');
insert into misspellings (correct, incorrect) values ('reference', 'refrence');
insert into misspellings (correct, incorrect) values ('referred', 'refered');
insert into misspellings (correct, incorrect) values ('referring', 'refering');
insert into misspellings (correct, incorrect) values ('relevant', 'relevent');
insert into misspellings (correct, incorrect) values ('relevant', 'revelant');
insert into misspellings (correct, incorrect) values ('religious', 'religius');
insert into misspellings (correct, incorrect) values ('religious', 'religous');
insert into misspellings (correct, incorrect) values ('remember', 'rember, remeber');
insert into misspellings (correct, incorrect) values ('repetition', 'repitition');
insert into misspellings (correct, incorrect) values ('resistance', 'resistence');
insert into misspellings (correct, incorrect) values ('restaurant', 'restarant');
insert into misspellings (correct, incorrect) values ('restaurant', 'restaraunt');
insert into misspellings (correct, incorrect) values ('rhyme', 'rime');
insert into misspellings (correct, incorrect) values ('rhythm', 'rythem');
insert into misspellings (correct, incorrect) values ('rhythm', 'rythm');
insert into misspellings (correct, incorrect) values ('secretary', 'secratary');
insert into misspellings (correct, incorrect) values ('secretary', 'secretery');
insert into misspellings (correct, incorrect) values ('seize', 'sieze');
insert into misspellings (correct, incorrect) values ('sense', 'sence');
insert into misspellings (correct, incorrect) values ('separate', 'seperate');
insert into misspellings (correct, incorrect) values ('sergeant', 'sargent');
insert into misspellings (correct, incorrect) values ('siege', 'seige');
insert into misspellings (correct, incorrect) values ('similar', 'similer');
insert into misspellings (correct, incorrect) values ('skilful', 'skilfull');
insert into misspellings (correct, incorrect) values ('skilful', 'skillful');
insert into misspellings (correct, incorrect) values ('speech', 'speach');
insert into misspellings (correct, incorrect) values ('speech', 'speeche');
insert into misspellings (correct, incorrect) values ('successful', 'succesful');
insert into misspellings (correct, incorrect) values ('successful', 'successfull');
insert into misspellings (correct, incorrect) values ('successful', 'sucessful');
insert into misspellings (correct, incorrect) values ('supersede', 'supercede');
insert into misspellings (correct, incorrect) values ('surprise', 'suprise');
insert into misspellings (correct, incorrect) values ('surprise', 'surprize');
insert into misspellings (correct, incorrect) values ('tattoo', 'tatoo');
insert into misspellings (correct, incorrect) values ('tendency', 'tendancy');
insert into misspellings (correct, incorrect) values ('than', 'then');
insert into misspellings (correct, incorrect) values ('their', 'there');
insert into misspellings (correct, incorrect) values ('their', 'they''re');
insert into misspellings (correct, incorrect) values ('therefore', 'therefor');
insert into misspellings (correct, incorrect) values ('threshold', 'threshhold');
insert into misspellings (correct, incorrect) values ('tomatoes', 'tomatos');
insert into misspellings (correct, incorrect) values ('tomorrow', 'tommorow');
insert into misspellings (correct, incorrect) values ('tomorrow', 'tommorow, tommorrow');
insert into misspellings (correct, incorrect) values ('tomorrow', 'tommorrow');
insert into misspellings (correct, incorrect) values ('tongue', 'tounge');
insert into misspellings (correct, incorrect) values ('truly', 'truely');
insert into misspellings (correct, incorrect) values ('twelfth', 'twelth');
insert into misspellings (correct, incorrect) values ('tyranny', 'tyrany');
insert into misspellings (correct, incorrect) values ('underrate', 'underate');
insert into misspellings (correct, incorrect) values ('unforeseen', 'unforseen');
insert into misspellings (correct, incorrect) values ('unfortunately', 'unfortunatly');
insert into misspellings (correct, incorrect) values ('until', 'untill');
insert into misspellings (correct, incorrect) values ('upholstery', 'upholstry');
insert into misspellings (correct, incorrect) values ('usable', 'useable');
insert into misspellings (correct, incorrect) values ('usable', 'usible');
insert into misspellings (correct, incorrect) values ('vacuum', 'vaccum');
insert into misspellings (correct, incorrect) values ('vacuum', 'vaccuum');
insert into misspellings (correct, incorrect) values ('vacuum', 'vacume');
insert into misspellings (correct, incorrect) values ('vehicle', 'vehical');
insert into misspellings (correct, incorrect) values ('vicious', 'visious');
insert into misspellings (correct, incorrect) values ('weather', 'whether');
insert into misspellings (correct, incorrect) values ('weird', 'wierd');
insert into misspellings (correct, incorrect) values ('welfare', 'welfair');
insert into misspellings (correct, incorrect) values ('welfare', 'wellfare');
insert into misspellings (correct, incorrect) values ('wherever', 'whereever');
insert into misspellings (correct, incorrect) values ('whether', 'wether');
insert into misspellings (correct, incorrect) values ('which', 'wich');
insert into misspellings (correct, incorrect) values ('wilful', 'wilfull');
insert into misspellings (correct, incorrect) values ('wilful', 'willful');
insert into misspellings (correct, incorrect) values ('withhold', 'withold');
insert into misspellings (correct, incorrect) values ('writing', 'writeing');
insert into misspellings (correct, incorrect) values ('writing', 'writting');
select * from misspellings;