# Android Spans

> [Kotlin](Span.kt) and [Java](Span.java) wrappers around SpannableStringBuilder.  
> Inspired by JakeWharton's [Truss](https://gist.github.com/JakeWharton/11274467) and Kotlin's [kotlinx.html](https://github.com/Kotlin/kotlinx.html)

<br>

**Supported Spans:**

`AbsoluteSizeSpan` `AlignmentSpan` `BackgroundColorSpan` `BulletSpan` `ClickableSpan` `DrawableMarginSpan` `ForegroundColorSpan` `IconMarginSpan` `ImageSpan` `LeadingMarginSpan` `MaskFilterSpan` `QuoteSpan` `RelativeSizeSpan` `ScaleXSpan` `StrikethroughSpan` `StyleSpan` `SubscriptSpan` `SuperscriptSpan` `TextAppearanceSpan` `TypefaceSpan` `URLSpan` `UnderlineSpan`

## Usage

### Kotlin

- Simple example:  Spans **are ~~hard?~~**
	```kotlin
	val span = span {
	    +"Spans "
	    bold {
	        +"are "
	        strikethrough { +"hard?" }
	    }
	}
	textView.text = span.build()
	```

- Advanced example
	```kotlin
	val span = span {
	    text("Regular text\n")
	    +"Or with the 'plus operator'\n"

	    /*Nested spans*/
	    bold {
	        backgroundColor(Color.BLACK) {
	            foregroundColor(Color.BLUE) {
	                +"Nested spans"
	            }
	        }
	        +" works too!\n"
	    }

	    /*Click callback*/
	    quote(Color.RED) {
	        url("https://google.com", { println(it.url); /*redirect?*/ true }) {
	            monospace {
	                +"Google"
	            }
	        }
	    }

	    /*Anonymous Span classes*/
	    strikethrough(object : StrikethroughSpan() {
	        override fun updateDrawState(ds: TextPaint?) {
	            super.updateDrawState(ds)
	            ds?.alpha = 50
	        }
	    }) {
	        +"\nstrikethrough with alpha"
	    }
	}
	textView.text = span.build()
	```

### Java

- Simple example: Spans **are ~~hard!~~**
	```java
	Span span = Span.create(
	    "Spans ",
	    bold(
	        "are ",
	        strikethrough("hard?")
	    )
	);
	textView.setText(span.build());
	```

- Advanced example
	```java
	Span span = Span.create(
	    text("Regular text\n"),
	    "Or with the String\n",

	    /*Nested spans*/
	    bold(
	        backgroundColor(Color.BLACK,
	            foregroundColor(Color.BLUE,
	                text("Nested spans")
	            )),
	        text(" works too!\n")
	    ),

	    /*Click callback*/
	    quote(
	        Color.RED,
	        url(
	            "https://google.com",
	            monospace(
	                text("Google")
	            )
	        ).onClick(new Span.Node.UrlSpanNode.OnClickCallback() {
	            @Override
	            public boolean onClick(URLSpan span) {
	                System.out.println(span.getURL());
	                return true;
	            }
	        })
	    ),

	    /*Anonymous Span classes*/
	    span(new StrikethroughSpan() {
	             @Override
	             public void updateDrawState(TextPaint ds) {
	             	super.updateDrawState(ds);
	                ds.setAlpha(50);
	            }
	        },
	        text("\nstrikethrough with alpha")
	    )
	);
	textView.setText(span.build());
	```

## License

```
Copyright 2017 Simon Marquis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
