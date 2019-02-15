#version 330 core
#define PI 3.141592
in vec3 inposition;
in vec3 innormal;
in vec2 intexcoord0;
in vec3 intangent;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

out vec3 VS_Position;
out vec3 VS_Normal;
out vec2 Texcoord0;
out vec3 WS_Normal;
out vec3 WS_Position;

void main() {
	gl_Position = projection * view * model *vec4(inposition, 1.0);
	VS_Position = (view * model * vec4(inposition,1.0)).xyz;
	VS_Normal =  normalize((view * model *vec4(innormal,0)).xyz);
	WS_Normal =  (model * vec4(innormal,0)).xyz;
	WS_Position = (model *vec4(inposition, 1.0)).xyz;
	Texcoord0 = intexcoord0;
}
