package icu.shiyixi.dailybackend.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtils {
    public static String entoken (Long userId){

        String token = "";
        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis()+TokenConstants.EXPIRE_DATE);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TokenConstants.TOKEN_SECRET);
            //设置头部信息
            Map<String,Object> header = new HashMap<>();
            header.put("typ","JWT");
            header.put("alg","HS256");
            //携带username，password信息，生成签名
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("userId", userId)
                    .withExpiresAt(date)
                    .sign(algorithm);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return  null;
        }
        return token;
    }
    public static Long detoken (String token) {
        return JWT.decode(token).getClaim("userId").asLong();
    }
    public static boolean verify(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(TokenConstants.TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }
}
