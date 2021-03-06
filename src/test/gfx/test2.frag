// Necessary Outputs
// 	* gl_FragColor : colour to actually draw the fragment.

// Available Inputs
//	* gl_Color		 : colour it would be drawn.
//	* gl_FragCoord   : location of fragment in window space.
//	* gl_PointCoord  :

const vec2 resolution = vec2(640, 480);
uniform float timeElapsed;

void main() {
	const vec3 randR = vec3(12.9898,78.233,91.327);
	const vec3 randG = vec3(78.233, 898.12233,91.327);
	const vec3 randB = vec3(512.9898,781.2323,9.279);
	float r = fract(sin(timeElapsed+dot(gl_FragCoord.xyz, randR)) * 1.0);
	float g = fract(sin(timeElapsed+dot(gl_FragCoord.xyz, randG)) * 437458.54153);
	float b = fract(sin(timeElapsed+dot(gl_FragCoord.xyz, randB)) * 458.54531);

	gl_FragColor = vec4(r, g, b, 1);

}
