#version 330 core
#define PI 3.141592
layout(location=0)out vec4 color;

in vec3 VS_Position;
in vec3 VS_Normal;
in vec3 WS_Normal;
in vec2 Texcoord0;
in vec3 WS_Position;

uniform vec4 baseColorFactor;
uniform sampler2D baseColorTexture;
uniform vec4 metallicRoughnessFactor;
uniform sampler2D roughnessTexture;
uniform sampler2D normalTexture;
uniform sampler2D occlusionTexture;
uniform vec4 textureFlags;

/*
* Struct types
*/
struct PerFragmentLightingArgument{
    vec3 normal;
    vec3 wNormal;
    vec3 view;
    vec3 reflect;
    vec3 wReflect;
    vec3 diffuseBase;
    vec3 specularBase;
    float reflectance;
    float reflectance90;
    float metallic;
    float roughness;
    float alpha;
    float nDotV;
};

struct PerLightLightingArgument{
    vec3 light;
    vec3 halfVector;
    float nDotL;
    float nDotH;
    float lDotH;
    float vDotH;
};


vec3 fresnel(PerFragmentLightingArgument frag,PerLightLightingArgument light){
    return vec3(frag.reflectance + (frag.reflectance90 - frag.reflectance) * pow(clamp(1.0 - light.vDotH, 0.0, 1.0), 5.0));
}

float geometric(PerFragmentLightingArgument frag,PerLightLightingArgument light){
    float NdotL = light.nDotL;
    float NdotV = frag.nDotV;
    float r = frag.alpha;

    float attenuationL = 2.0 * NdotL / (NdotL + sqrt(r * r + (1.0 - r * r) * (NdotL * NdotL)));
    float attenuationV = 2.0 * NdotV / (NdotV + sqrt(r * r + (1.0 - r * r) * (NdotV * NdotV)));
    return attenuationL * attenuationV;
}

float distribution(PerFragmentLightingArgument frag,PerLightLightingArgument light){
    float roughnessSq = frag.alpha * frag.alpha;
    float f = (light.nDotH * roughnessSq - light.nDotH) * light.nDotH + 1.0;
    return roughnessSq / (PI * f * f);
}

vec3 lightingPerLight(PerFragmentLightingArgument frag,PerLightLightingArgument light){
    vec3 F = fresnel(frag,light);
    float G = geometric(frag,light);
    float D = distribution(frag,light);

    vec3 diffuse = (1.0 - F) * frag.diffuseBase / PI;
    vec3 specular = F*G*D/(4.0 * light.nDotL * frag.nDotV);
    return diffuse + specular;
}

vec3 lighting(PerFragmentLightingArgument fragmentArg){
    vec3 result = vec3(0.);
    // TODO: Support multiple lights by for loop here
    // Compute lighting parameter depends lights
    vec3 lightDir = normalize(vec3(1,1,0));
    vec3 halfVector = normalize(lightDir + fragmentArg.view);
    float nDotL = clamp(dot(fragmentArg.normal,lightDir),0.001,1.0);
    float nDotH = clamp(dot(fragmentArg.normal,halfVector),0.,1.);
    float lDotH = clamp(dot(lightDir,halfVector),0.,1.);
    float vDotH = clamp(dot(fragmentArg.view,halfVector),0.,1.);
    PerLightLightingArgument lightArg = PerLightLightingArgument(lightDir,halfVector,nDotL,nDotH,lDotH,vDotH);
    result += nDotL * vec3(1,1,1) *lightingPerLight(fragmentArg,lightArg);
    return result;//vec3(lDotH);
}

mat3 cotangent_frame(vec3 N, vec3 p, vec2 uv)
{
    // get edge vectors of the pixel triangle
    vec3 dp1 = dFdx( p );
    vec3 dp2 = dFdy( p );
    vec2 duv1 = dFdx( uv );
    vec2 duv2 = dFdy( uv );

    // solve the linear system
    vec3 dp2perp = cross( dp2, N );
    vec3 dp1perp = cross( N, dp1 );
    vec3 T = dp2perp * duv1.x + dp1perp * duv2.x;
    vec3 B = dp2perp * duv1.y + dp1perp * duv2.y;

    // construct a scale-invariant frame
    float invmax = inversesqrt( max( dot(T,T), dot(B,B) ) );
    return mat3( T * invmax, B * invmax, N );
}

vec4 srgb2linear(vec4 srgb){
    return vec4(pow(srgb.rgb,vec3(2.2)),srgb.a);
}

void main() {
    vec4 baseColor = baseColorFactor;
    if(textureFlags.r == 1.0){
        vec4 baseColorTex = texture(baseColorTexture,Texcoord0);
        baseColor*=srgb2linear(baseColorTex);
    }
    float metallic = metallicRoughnessFactor.r;
    float roughness = metallicRoughnessFactor.g;
    if(textureFlags.g == 1.0){
        vec4 metallicRoughnesstex = texture(roughnessTexture,Texcoord0);
        //metallic *= metallicRoughnesstex.b;
        //roughness *= metallicRoughnesstex.g;
    }
    // Clamp roughness as a perceptual value
    roughness = clamp(roughness,0.04,1.0);
    metallic = clamp(metallic,0.0,1.0);
    float alpha = roughness * roughness;
    vec3 f0 = vec3(0.04);
    vec3 diffuseBase = baseColor.rgb * (vec3(1.0) - f0);
    diffuseBase *= 1.0 - metallic;
    vec3 specularBase = mix(f0,baseColor.rgb,metallic);
    float reflectance = max(max(specularBase.r,specularBase.g),specularBase.b);
    float reflectance90 = clamp(reflectance * 25.0 ,0.0 ,1.0);

  // Compute arguments that is independent of lights
    vec3 normal = normalize(VS_Normal);
    // Normal mapping
    if(textureFlags.b == 1.0){
        vec3 tsNormal = texture(normalTexture,Texcoord0).rgb;
        tsNormal = normalize(tsNormal * 2.0 - 1.0); // [0,1] to [-1,1]
        mat3 perFragmentCotFrame = cotangent_frame(normal,VS_Position,Texcoord0);
        normal = normalize(perFragmentCotFrame * tsNormal);
    }
    // View vector is same as the negated vsPosition because all of the vectors are in view space
    // , camera position in camera space will be zero vector naturally.
    vec3 v = normalize(-VS_Position);
    vec3 r = -normalize(reflect(v,normal));
    vec3 wr = -normalize(reflect(normalize(vec3(0,0,5) - WS_Position),WS_Normal));
    float nDotV = clamp(abs(dot(normal,v)),0.001,1.0);

    // Construct lighting param
    PerFragmentLightingArgument pFrag = PerFragmentLightingArgument(normal,WS_Normal,v,r,wr,diffuseBase,specularBase,reflectance,reflectance90,metallic,roughness,alpha,nDotV);

    // Lighting
    vec3 result = lighting(pFrag);

    // Ambient Occlusion
    if(textureFlags.a == 1.0){
        float occlusion = texture(occlusionTexture,Texcoord0).r;
        result*=occlusion;
    }
	color = vec4(pow(result,vec3(1.0/5.)),baseColor.a);
}
