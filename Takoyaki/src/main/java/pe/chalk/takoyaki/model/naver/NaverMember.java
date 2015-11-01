package pe.chalk.takoyaki.model.naver;

import pe.chalk.takoyaki.model.Member;
import pe.chalk.takoyaki.target.Target;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-11-01
 */
public class NaverMember extends Member {
    public NaverMember(Target target, String username, String nickname){
        super(target, username, nickname);
    }

    @Override
    public String getHost(){
        return "naver.com";
    }
}
