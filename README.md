busviewer for Android
=========
Android版石川バスビュワー（旧北鉄バスビュワー）だったソースコードです。

#概要
2011年3月17日最初のAndroid版リリースをいたしました。
2012年1月29日iPhone(iOS)版をリリースしました。

約3年間提供してきまして、2014年3月31日に終了を迎えます。
突然の終了のお詫びと今までの感謝を込め、Android版のソースコードを公開します。

振り返ると、Androidアプリを始めたばかりだった時のコードもございますので、
突っ込みどころなどもあると存じます。
現在のスキルではもっとスマートに書けると思います。
(Android-AnnotationsやGreenDAOを導入するなど）

## 注意
このソースコードをビルドしても、特に何も出来ません。
メインのロジックはサーバーサイドだったため、アプリ側には特に重要なコードはないと考えています。（なので公開致しました）


##依存ライブラリ
* [ActionBarSherlock](http://actionbarsherlock.com/)  Apache License Version 2.0
* [ViewPagerIndicator](http://viewpagerindicator.com/) Apache License Version 2.0
* android-support-v4.jar
* google-play-services.jar

##含まれていないもの
* アプリのアイコン
* 広告(Admob)周りのコード
* 時刻表/バス停データ（サーバーサイドで提供していたため）
* アクセスポイントURL
* Android Google Map V2 ID
* keystore(もちろん）


# ライセンス
（書く必要がない気もしますが、書かなかった際に面倒なことになってもあれなので）
ライセンスはMPLで提供します。コード1行単位での利用も可能です。
利用した旨を記述していただければ大丈夫です。
商用利用の場合も基本的にはOKですが、利用される前に要連絡でお願いします。

もう利用されることはないという想定でコードの公開をしておりますが、
もし、利用したいとお考えの方は、ご連絡ください。ご相談させていただきます。
bus.viewer.team@gmail.com

#謝辞
このプロジェクトの開発にご協力いただいた方々

* T.Sさん
* S.Kさん
* T.Sさん
* H.Yさん
* T.Hさん
* R.Yさん
* R.Mさん
* @Azami_Chan

また、他にも多くの方にアドバイス・ご協力頂きました。

* Cafe?IKAGAWADOオーナー 五十川 員申 様

* CENDO Inc. 宮田 人司 様

* アイコン作成  イロドリ様 http://irodo-ri.com/

 など多数の方々

#最後に

これからも石川のバスの利用が更に便利になるように祈っております。

今までありがとうございました。


金沢ではITやデザインの力で、私たち市民の生活が今よりも良くなることを目指している
[Code for Kanazawa](http://www.codeforkanazawa.org)が始まりました。

そのプロジェクトとして、
[5374](http://5374.jp/)が開発され、

[kanazawa.5374](http://kanazawa.5374.jp/)や
[nonoichi.5374](http://nonoichi.5374.jp/)が開発されました。
（5374のメインプログラマーです）

また、個人のプロジェクトとして
[金沢市各地区ゴミ収集日カレンダー](http://yuki2006.github.io/gomi_kanazawa_ical/)
を作成しました。ぜひご利用ください。


