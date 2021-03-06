/**
 * (c) Copyright 2018, 2019 IBM Corporation
 * 1 New Orchard Road, 
 * Armonk, New York, 10504-1722
 * United States
 * +1 914 499 1900
 * support: Nathaniel Mills wnm3@us.ibm.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.api.jsonata4java.expressions.functions;

import com.api.jsonata4java.expressions.EvaluateRuntimeException;
import com.api.jsonata4java.expressions.ExpressionsVisitor;
import com.api.jsonata4java.expressions.PropertyDoesNotExistEvaluateRuntimeException;
import com.api.jsonata4java.expressions.generated.MappingExpressionParser.Function_callContext;
import com.api.jsonata4java.expressions.utils.Constants;
import com.api.jsonata4java.expressions.utils.FunctionUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class ExistsFunction extends FunctionBase implements Function {

	public static String ERR_BAD_CONTEXT = String.format(Constants.ERR_MSG_BAD_CONTEXT, Constants.FUNCTION_EXISTS);
	public static String ERR_ARG2BADTYPE = String.format(Constants.ERR_MSG_ARG2_BAD_TYPE, Constants.FUNCTION_EXISTS);

	public JsonNode invoke(ExpressionsVisitor expressionVisitor, Function_callContext ctx) {
		// Create the variable to return
		JsonNode result = null;

		// Retrieve the number of arguments
		JsonNode arg = JsonNodeFactory.instance.nullNode();
		boolean useContext = FunctionUtils.useContextVariable(ctx, getSignature());
		int argCount = getArgumentCount(ctx);
		if (useContext) {
			arg = FunctionUtils.getContextVariable(expressionVisitor);
			argCount++;
		}

		// Make sure that we have the right number of arguments
		if (argCount == 1) {
			if (!useContext) {
				try {
					arg = FunctionUtils.getValuesListExpression(expressionVisitor, ctx, 0);
				} catch (PropertyDoesNotExistEvaluateRuntimeException pdne) {
					return BooleanNode.FALSE;
				} catch (NullPointerException npe) {
					return BooleanNode.FALSE;
				} catch (EvaluateRuntimeException ere) {
					return BooleanNode.FALSE;
				}
			}
			// evaluate the expression the property exists iff a
			// PropertyDoesNotExistEvaluateRuntimeException is NOT thrown
			// NOTE: $exists(null) returns true according to
			// http://try.jsonata.org/
			if (arg == null) {
				result = BooleanNode.FALSE;
			} else {
				result = BooleanNode.TRUE;
			}
		} else {
			throw new EvaluateRuntimeException(argCount == 0 ? ERR_BAD_CONTEXT : ERR_ARG2BADTYPE);
		}

		return result;
	}

	@Override
	public String getSignature() {
		// accepts a number (or context variable), returns a number
		return "<n-:n>";
	}
}
