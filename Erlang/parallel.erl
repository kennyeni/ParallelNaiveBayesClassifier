-module(parallel).
-compile(export_all).


read_file(File) ->
	{ok, Data} = file:read_file(File),
	binary:split(Data, [<<"\n">>], [global]).

cero_uno_dc(X) ->
	if 
        X == "1" ->
            uno;
        X == "0" ->
            cero;
        true ->
            dc
    end.


label(Class, [], Res, _Count) -> 
	SClass = string:concat("class_", Class),
	[{SClass, 1}, Res];

label(Class, Features, Res, Count) ->
	[CurrVal | Rest] = Features,
	New = {string:concat(Class, integer_to_list(Count)), binary_to_list(CurrVal)},
	{Key, Val} = New,
	case cero_uno_dc(Val) of
		cero -> 
			New2 = {Key, 0},
			label(Class, Rest, [Res, New2], Count + 1);
		uno -> 
			New2 = {Key, 1},
			label(Class, Rest, [Res, New2], Count + 1);
		dc ->
			label(Class, Rest, Res, Count + 1)
	end.
	
	


label_features(List) ->
	[Class|Features] = List,
	SClass = binary_to_list(Class),
	Labeled = label(SClass, Features, [], 0),
	lists:flatten(Labeled).


naive_bayes_model() ->
	TraningTuples = read_file("babygenestrain.tab"),
	D = plists:map(
		fun(X) -> % For each tuple
			List = binary:split(X, [<<"\t">>], [global]), % Split by tabs
			label_features(List)
		end,
		TraningTuples
	),
	ModelList = lists:flatten(D),
	ModelDictMid = plists:mapreduce(
		fun(X) -> X end,
		ModelList
	),
	ModelDictMid2 = dict:to_list(ModelDictMid),
	Model = plists:map(
		fun(X) ->
			{Key, List} = X,
			{Key, lists:foldl(
				fun(Y, Acc) -> 
					Acc + Y 
					end, 
					0, List
				)
			}
		end,
		ModelDictMid2
	),
	dict:from_list(Model).

probab(_Class, [], Res, _Count) -> Res;

probab(Class, Features, Res, Count) ->
	[CurrVal | Rest] = Features,
	Val = binary_to_list(CurrVal),
	Key = string:concat(Class, integer_to_list(Count)),
	case cero_uno_dc(Val) of
		cero -> 
			probab(Class, Rest, Res, Count + 1);
		uno -> 
			probab(Class, Rest, Res ++ [{Key, 1}], Count + 1);
		dc ->
			probab(Class, Rest, Res, Count + 1)
	end.

probab_features(Classes, List) ->
	lists:map(
		fun(C) ->
			probab(C, List, [], 0)
		end,
		Classes
		).

get_probs(Y, Dictionary) ->
	[K|_P] = Y,
	{Key1, _G} = K,
	ClassNa = string:substr(Key1, 1, 3),
	ClassName = string:concat("class_", ClassNa),
	ClassCount = case dict:find(ClassName, Dictionary) of
		{ok, Value} -> Value;
		_ -> error 
	end,
	FreqList = lists:map(
		fun(Z) -> % Z = x
			{Key, _H} = Z,  
			case dict:find(Key, Dictionary) of
				{ok, Val} -> Val + 0.007;
				_ -> 0.007
			end
		end,
		Y
		),
	FreqListLen = length(FreqList),
	Pow = math:pow(ClassCount, FreqListLen -1),
	[{ClassName, Pow}] ++ FreqList. % [{Class, Pow}, Features]

final_multiplication(Info, Feats) ->
	{ClassFromInfo, _P} = Info,
	Probs = lists:foldl(
		fun(Mult, Acc) ->
			Mult * Acc
		end,
		1, Feats
	),
	{ClassFromInfo, Probs}.

features_to_probabilities(List, Dictionary) ->
	plists:map(
		fun(X) -> % X = [[] []]
			lists:map(
				fun(Y) -> % Y = []
					SubFinalTuple = case length(Y) of
						0 -> [zero];
						_ -> get_probs(Y, Dictionary)
					end,
					[Info|Feats] = SubFinalTuple,

					case Info of
						zero -> {zero};
						_ -> final_multiplication(Info, Feats)
					end
				end,
				X
			) 
		end,
		List
	).

naive_bayes_classifier(Classes, Dictionary) ->
	ClassifyTuples = read_file("babygenesblind.tab"),
	% Tuples of each Classfying tuples, concatentaed eith possible classes
	TupleFeatures = plists:map( 
		fun(X) -> % For each tuple
			List = binary:split(X, [<<"\t">>], [global]), % Split by tabs
			probab_features(Classes, List)
		end,
		ClassifyTuples
	),
	features_to_probabilities(TupleFeatures, Dictionary).

naive_bayes(Classes) ->
	Dictionary = naive_bayes_model(),
	naive_bayes_classifier(Classes, Dictionary).


