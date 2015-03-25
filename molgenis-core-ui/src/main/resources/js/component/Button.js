/* global _: false, React: false, molgenis: true */
(function(_, React, molgenis) {
	"use strict";
	
	var button = React.DOM.button, a = React.DOM.a;
	
	/**
	 * @memberOf component
	 */
	var Button = React.createClass({
		mixins: [molgenis.ui.mixin.DeepPureRenderMixin],
		displayName: 'Button',
		propTypes: {
			id : React.PropTypes.string,
			type: React.PropTypes.oneOf(['button', 'submit', 'reset']),
			style: React.PropTypes.oneOf(['default', 'primary', 'success', 'info', 'warning', 'danger', 'link']),
			size: React.PropTypes.oneOf(['xsmall', 'small', 'medium', 'large']),
			text: React.PropTypes.string,
			icon: React.PropTypes.string,
			css: React.PropTypes.object,
			name: React.PropTypes.string,
			value: React.PropTypes.string,
			disabled : React.PropTypes.bool,
			onClick: React.PropTypes.func,
		},
		getDefaultProps: function() {
			return {
				type: 'button',
				style: 'default',
				size: 'medium'
			};
		},
		render: function() {
			var buttonClasses = 'btn btn-' + this.props.style;
			switch(this.props.size) {
				case 'xsmall':
					buttonClasses += ' btn-xs';
					break;
				case 'small':
					buttonClasses += ' btn-sm';
					break;
				case 'medium':
					break;
				case 'large':
					buttonClasses += ' btn-lg';
					break;
				default:
					throw 'Unknown Button style [' + this.props.style + ']';
			}
			
			if(this.props.style !== 'link') {
				var buttonProps = {
						className: buttonClasses,
						id : this.props.id,
						type : this.props.type,
						name: this.props.name,
						style: this.props.css,
						disabled : this.props.disabled,
						value : this.props.value,
						onClick : this.props.onClick
					};
				
				return (
					button(buttonProps,
						this.props.icon ? molgenis.ui.Icon({name: this.props.icon}) : null,
						this.props.text ? (this.props.icon ? ' ' + this.props.text : this.props.text) : null
					)
				);
			} else {
				if(this.props.disabled) {
					buttonClasses += ' disabled';
				}
				var anchorProps = {
						className: buttonClasses,
						href: '#',
						role: 'button',
						id : this.props.id,
						name: this.props.name,
						style: this.props.css,
						value : this.props.value,
						onClick : this.props.onClick
					};
				
				return (
					a(anchorProps,
						this.props.icon ? molgenis.ui.Icon({name: this.props.icon}) : null,
						this.props.text ? (this.props.icon ? ' ' + this.props.text : this.props.text) : null
					)
				); 
			}
		}
	});
	
	// export component
	molgenis.ui = molgenis.ui || {};
	_.extend(molgenis.ui, {
		Button: React.createFactory(Button)
	});
}(_, React, molgenis));