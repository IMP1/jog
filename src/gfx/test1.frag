// Necessary Outputs
// 	* gl_FragColor : colour to actually draw the fragment.

// Available Inputs
//	* gl_Color		 : colour it would be drawn.
//	* gl_FragCoord   : location of fragment in window space.
//	* gl_PointCoord  :

const vec2 resolution = vec2(640, 480);
uniform float timeElapsed;

void main() {
	vec3 col = vec3(0, 0, 0);
	const int itrCount = 8;
	for (int i = 0; i < itrCount; ++i) {
		float offset = float(i) / float(itrCount);
		float t = timeElapsed + (offset * offset * 2.);

		vec2 pos = (gl_FragCoord.xy/resolution.xy);
		pos.y -= 0.5;
		pos.y += sin(pos.x * 9 + t) * 0.2 * sin(t * 0.8);
		float color = 1 - pow(abs(pos.y), 0.2);
		float colora = pow(1, 0.2 * abs(pos.y));

		float rColMod = ((offset * 0.5) + 0.5) * colora;
		float bColMod = (1 - (offset * 0.5) + 0.5) * colora;

		col += vec3(color * rColMod, color, color * bColMod) * (1. / float(itrCount));
	}
	col = clamp(col, 0, 1);
	gl_FragColor = vec4(col.x, col.y, col.z ,1.0);
}
