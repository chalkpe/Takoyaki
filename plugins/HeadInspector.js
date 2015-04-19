/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-19
 */

importPackage(Packages.pe.chalk.takoyaki);
importPackage(Packages.pe.chalk.takoyaki.data);
importPackage(Packages.pe.chalk.takoyaki.filter);
importPackage(Packages.pe.chalk.takoyaki.logger);

function TargetMap(targetAddress, menuIds, members){
    this.targetAddress = targetAddress;
    this.menuIds = menuIds;
    this.members = members;
}

TargetMap.prototype = {
    check: function(articleArray){
        var that = this;
        articleArray.forEach(function(article){
            if(article.getTarget().getAddress().equalsIgnoreCase(that.targetAddress) && that.menuIds.indexOf(article.getMenuId()) >= 0){
                Mailer.send("mcpekorea.takoyaki@gmail.com", "메롱", "[타코야키] 말머리 미설정 게시글", article.toString(), Packages.java.util.Arrays.asList(that.members));
            }
        });
    }
};

var maps = [
    new TargetMap("ourmcspace", [
        145, //과제추천란
        146, //과제해금란
        147, //명예의전당
        69,  //질문게시판
        48,  //마크소식지
        51,  //마크공략란
        52,  //베스트공략
        53,  //레드스톤　
        55,  //통합자료　
        56,  //스킨　　　
        57,  //세이브파일
        58,  //리소스팩　
        59,  //모드　　　
        60,  //플러그인　
        61   //요청게시판
    ], [new Member(null, "법관 전현수", "clone_jhs")]),

    new TargetMap("ourmcspace", [
        111, //정품서버란
        113, //서버홍보란
        115, //구인구직란
        157  //사설이벤트
    ], [new Member(null, "BriskWind", "ysm980407")]),

    new TargetMap("ourmcspace", [
        156, //창작작품란
        98,  //유머※짤방
        99   //　　쿵쿵따
    ], [new Member(null, "초차원", "kwoobin123")])
];

function onDataUpdated(filter, data){
    if(filter instanceof ArticleFilter){
        maps.forEach(function(map){
            map.check(data);
        })
    }
}