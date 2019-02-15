
//#version 120
//
// simple.frag
//
uniform mat4 mat[4];
uniform sampler2D texture0;
uniform vec3 lightcolor;
varying vec3 normal;
varying vec4 color;
varying vec2 texcoord;
varying vec3 viewdir;
varying vec3 lightdir;

void main (void){
//  gl_FragColor = max(texture2D(texture0, texcoord),color.xyzw);
//  gl_FragColor = texture2D(texture0, texcoord);
//  gl_FragColor = vec4(0.3, 0.8, 0.2, 1.0);
//  vec2 dfdx = abs(dFdx(texcoord));
//  vec2 dfdy = abs(dFdy(texcoord));
//  float d = max(max(dfdx.x,dfdx.y),max(dfdy.x,dfdy.y));
//  gl_FragColor = vec4(d*20,0,0,1);

  //vec3 diffuse = vec3(0.4,0.4,0.25);
  vec3 diffuse = color.xyz;
  vec3 specular = lightcolor;
  vec3 ambient = 0.3*diffuse;
  vec3 bump = (texture2D(texture0, texcoord)*2.0-1.0).xyz;
  vec3 v = normalize(viewdir);
  vec3 l = normalize(lightdir);
  vec3 n = normalize(normal+0.15*bump);
  //vec3 n = normalize(normal);
  vec3 h = (l+v)/2.0;
  float nh = max(dot(n, h),0.0);
  float ks = 170.5*pow(nh,2.1);
  vec3 reflection = (0.8*max(dot(l,n),0.0))*diffuse+ks*specular+ambient;
  gl_FragColor = vec4(reflection,1.0);
  //  gl_FragColor = vec4(1.0,0.8,0.3,1.0);
}
